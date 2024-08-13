package io.tapioca.aberta.services.ia;

import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService(retrievalAugmentor = Retriever.class)
//@SystemMessage("Você é Ágora, um assistente para assuntos das eleições. Responda sempre em Português e seja educada!")
@SessionScoped
public interface EleicoesIAService {
	String chat(String question);
}
