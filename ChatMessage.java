package com.sheoran.chatapplication.model;

public class ChatMessage {
    public enum Type { CHAT, JOIN, LEAVE, TYPING, PRIVATE }

    private Type type;
    private String sender;
    private String content;
    private String toUser; // for private messages
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(Type type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // getters / setters
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
