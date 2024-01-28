package br.com.seteideias.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static java.time.LocalTime.now;

@SpringBootApplication
public class WebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketApplication.class, args);
    }

    record GrettingRequest(String name) {}

    record GreetingResponse(String message) {}

    @Controller
    public record GreetingsWebSocketController(
            SimpMessagingTemplate simpMessagingTemplate) {

        @MessageExceptionHandler
        @SendTo("/topic/errors")
        String handleException(Exception e) {
            final var message = "Something went wrong processing request.: " + NestedExceptionUtils.getMostSpecificCause(e);
            System.out.println(message);
            return message;
        }

        @MessageMapping("/chat")
        @SendTo("/topic/greetings")
        GreetingResponse greet(GrettingRequest grettingRequest) {
            Assert.isTrue(Character.isUpperCase(grettingRequest.name().charAt(0)), () -> "the name must start with capital letter!");
//            Thread.sleep(1_000);
            System.out.println(">>>>>>>>>>>>>>>> " + grettingRequest.name());
            return new GreetingResponse("Hello, " + grettingRequest.name() + " >> " + now() + " - !");
        }
    }

    @Configuration
    @EnableWebSocketMessageBroker
    class GreetingWebSockerConfiguration implements WebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/topic");
            registry.setApplicationDestinationPrefixes("/app");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
//            registry.addEndpoint("/chat");
            registry.addEndpoint("/chat").withSockJS();
        }
    }

}
