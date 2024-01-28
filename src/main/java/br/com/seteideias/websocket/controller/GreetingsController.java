package br.com.seteideias.websocket.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record GreetingsController(SimpMessagingTemplate messagingTemplate) {

    @PostMapping("/send-greeting")
    public void sendGreeting(@RequestBody String message) {
        messagingTemplate.convertAndSend("/topic/greetings", message);
    }
}
