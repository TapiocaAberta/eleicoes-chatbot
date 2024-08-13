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
        return "Welcome, my name is Mona, how can I help you today?";
    }

    @OnTextMessage
    String onMessage(String message) {
        return bot.chat(message);
    }

}
