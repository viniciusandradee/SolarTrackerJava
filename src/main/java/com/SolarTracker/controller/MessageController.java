package com.SolarTracker.controller;

import com.SolarTracker.service.MessageProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        messageProducer.sendMessage(message);
        return ResponseEntity.ok("Mensagem enviada: " + message);
    }
}
