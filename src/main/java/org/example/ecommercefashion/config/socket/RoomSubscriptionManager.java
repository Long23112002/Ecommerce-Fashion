package org.example.ecommercefashion.config.socket;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomSubscriptionManager {

    private final ConcurrentHashMap<String, Set<Long>> roomSubscriptions = new ConcurrentHashMap<>();

    public void addUserToRoom(String roomId, Long userId) {
        roomSubscriptions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public void removeUserFromRoom(String roomId, Long userId) {
        Set<Long> users = roomSubscriptions.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                roomSubscriptions.remove(roomId);
            }
        }
    }

    public Set<Long> getUsersInRoom(String roomId) {
        return roomSubscriptions.getOrDefault(roomId, ConcurrentHashMap.newKeySet());
    }

    public void removeUserFromAllRooms(Long userId) {
        roomSubscriptions.forEach((roomId, users) -> {
            if (users.contains(userId)) {
                users.remove(userId);
                System.out.println("Removed user " + userId + " from room " + roomId);
                if (users.isEmpty()) {
                    roomSubscriptions.remove(roomId);
                }
            }
        });
    }
}