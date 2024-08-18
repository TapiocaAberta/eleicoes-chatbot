package io.tapioca.aberta.services.ia;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService(retrievalAugmentor = Retriever.class)
@SystemMessage("You are ÁgorAI! Always answer in Portuguese.")
@SessionScoped
public interface EleicoesIAService {
	String chat(@UserMessage String question);
}
