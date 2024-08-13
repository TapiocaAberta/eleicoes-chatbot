package io.tapioca.aberta.socket;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.tapioca.aberta.services.ia.EleicoesIAService;
import jakarta.inject.Inject;

@WebSocket(path = "/chat")
public class ChatWebSocket {
	
	@Inject EleicoesIAService bot;

    @OnOpen
    String welcome() {
        return "Olá, eu sou ÁgorAI, quais são suas dúvidas sobre as eleições Municipais?";
    }

    @OnTextMessage
    String onMessage(String message) {
        return bot.chat(message);
    }

}
