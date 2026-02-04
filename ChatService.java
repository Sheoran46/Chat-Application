package com.sheoran.chatapplication.service;

import com.sheoran.chatapplication.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class ChatService {

    // online users -> mapped to sessionId or username, using username for simplicity
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    // message history (bounded)
    private final Deque<ChatMessage> history = new ConcurrentLinkedDeque<>();
    private final int HISTORY_LIMIT = 200;

    public void addUser(String username) {
        onlineUsers.add(username);
    }

    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }

    public void addMessage(ChatMessage msg) {
        history.addLast(msg);
        if (history.size() > HISTORY_LIMIT) {
            history.removeFirst();
        }
    }

    public List<ChatMessage> getRecentMessages(int n) {
        List<ChatMessage> list = new ArrayList<>(history);
        int size = list.size();
        if (n >= size) return list;
        return list.subList(size - n, size);
    }
}
