package com.sheoran.chatapplication.controller;

import com.sheoran.chatapplication.model.ChatMessage;
import com.sheoran.chatapplication.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    // Called when client sends to /app/sendMessage
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // broadcast chat
        if (chatMessage.getType() == null) chatMessage.setType(ChatMessage.Type.CHAT);

        if (chatMessage.getType() == ChatMessage.Type.PRIVATE && chatMessage.getToUser() != null) {
            // private message: send to user queue
            messagingTemplate.convertAndSendToUser(chatMessage.getToUser(), "/queue/messages", chatMessage);
            messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/messages", chatMessage);
            // optionally store private into history
            chatService.addMessage(chatMessage);
        } else {
            // normal broadcast
            chatService.addMessage(chatMessage);
            messagingTemplate.convertAndSend("/topic/messages", chatMessage);
        }
    }

    // When a user joins, client sends a join message (to /app/join)
    @MessageMapping("/join")
    public void join(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();
        headerAccessor.getSessionAttributes().put("username", username);
        chatService.addUser(username);

        ChatMessage join = new ChatMessage(ChatMessage.Type.JOIN, username, username + " joined");
        messagingTemplate.convertAndSend("/topic/messages", join);
        // send updated user list
        messagingTemplate.convertAndSend("/topic/users", chatService.getOnlineUsers());
    }

    @MessageMapping("/leave")
    public void leave(@Payload ChatMessage chatMessage) {
        String username = chatMessage.getSender();
        chatService.removeUser(username);

        ChatMessage leave = new ChatMessage(ChatMessage.Type.LEAVE, username, username + " left");
        messagingTemplate.convertAndSend("/topic/messages", leave);
        messagingTemplate.convertAndSend("/topic/users", chatService.getOnlineUsers());
    }

    @MessageMapping("/typing")
    public void typing(@Payload ChatMessage chatMessage) {
        // broadcast typing events (type= TYPING)
        messagingTemplate.convertAndSend("/topic/typing", chatMessage);
    }

    // rest endpoint to fetch recent messages
    @GetMapping("/messages/recent")
    @ResponseBody
    public List<ChatMessage> recent(@RequestParam(defaultValue = "50") int n) {
        return chatService.getRecentMessages(n);
    }

    // rest endpoint to fetch online users
    @GetMapping("/users")
    @ResponseBody
    public Set<String> users() {
        return chatService.getOnlineUsers();
    }

    // serve the page
    @GetMapping("/chat")
    public String chatPage() {
        return "index";
    }
    @GetMapping("/")
    public String chatPage2() {
        return "index";
    }
}
