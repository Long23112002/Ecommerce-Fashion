package org.example.ecommercefashion.config.socket;

public enum WebSocketDestination {
    CHAT_ROOM("/chat/room"),
    CHAT_ADMIN("/chat/admin"),
    NOTIFICATION("/notification/user")
    ;

    private final String destination;

    WebSocketDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestinationWithSlash() {
        return destination+"/";
    }
}
