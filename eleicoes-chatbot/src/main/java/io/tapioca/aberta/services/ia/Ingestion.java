package io.tapioca.aberta.services.ia;

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
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
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

		Path dir = Path.of("../data/");
        List<Document> documents = new ArrayList<>();
        
        if (!isDirectory(dir)) {
            throw illegalArgument("'%s' is not a directory", dir);
        }
        
        try (Stream<Path> pathStream = Files.list(dir)) {
            
        	pathStream.forEach(p -> {
        		try {
        			
        			String content = Files.readString(p);
        			var start = content.indexOf("<metadata:start>");
        			var end = content.indexOf("<metadata:end>");
        			var metadataText = content.substring(start, end);
        			
					String[] split = metadataText.replace("<metadata:start>", "").split(";");
        			
        			Map<String, String> metadata = new HashMap<>();
        			metadata.put("cargo", split[0]);
					metadata.put("nome", split[1]);
					metadata.put("partido", split[2]);
					metadata.put("partido_sigla", split[3]);
        			documents.add(new Document(content, Metadata.from(metadata)));
					
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
        	});
        	
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
		DocumentSplitter documentSplitter = new DocumentByLineSplitter(1300, 0);
		documentSplitter.splitAll(documents);
		
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
																.embeddingStore(store)
																.embeddingModel(embedding)
																.documentSplitter(documentSplitter)
																.build();
        
        Log.info("Ingesting " + documents.size() + " documents");
        ingestor.ingest(documents);
        Log.info("Document ingested");
        
    }

}
