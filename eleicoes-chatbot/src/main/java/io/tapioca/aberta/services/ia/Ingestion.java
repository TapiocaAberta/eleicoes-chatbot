package io.tapioca.aberta.services.ia;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
import static dev.langchain4j.internal.Exceptions.illegalArgument;
import static java.nio.file.Files.isDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.inject.Singleton;

@Startup
@Singleton
public class Ingestion {
	
	public Ingestion(EmbeddingStore<TextSegment> store, EmbeddingModel embedding) { 

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embedding)
                .documentSplitter(recursive(1500, 0))  
                .build();
        
        Path dir = Path.of("../data/");
        List<Document> documents = new ArrayList<>();
        
        if (!isDirectory(dir)) {
            throw illegalArgument("'%s' is not a directory", dir);
        }
        
        try (Stream<Path> pathStream = Files.list(dir)) {
        
        	pathStream.forEach(p -> {
        		try {        			
					List<String> lines = Files.readAllLines(p);
					
					String fileName = p.getFileName().toString();
					
					Log.info("File: " + fileName);
					
					if(fileName.equals("prefeitos.txt")) {
						Log.info("Arquivo de Prefeitos ....");
						documents.add(new Document(lines.stream().collect(Collectors.joining(" ")), Metadata.from("conteudo", "lista de prefeitos")));
					} else if(p.getFileName().toString().equals("vereadores.txt")) {
						Log.info("Arquivo de Vereadores ....");
						documents.add(new Document(lines.stream().collect(Collectors.joining(" ")), Metadata.from("conteudo", "lista de vereadores")));
					} else {
						Map<String, String> metadata = new HashMap<>();
						metadata.put("cargo", lines.get(0).replace("Cargo: ", ""));
						metadata.put("nome", lines.get(1).replace("Nome: ", ""));
						metadata.put("partido", lines.get(2).replace("Partido: ", ""));
						metadata.put("partido_sigla", lines.get(3).replace("Sigla: ", ""));
						documents.add(new Document(lines.stream().collect(Collectors.joining(" ")), Metadata.from(metadata)));
					}
					
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
        	});
        	
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Log.info("Ingesting " + documents.size() + " documents");
        ingestor.ingest(documents);
        Log.info("Document ingested");
        
    }

}
