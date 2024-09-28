package org.example.ecommercefashion.config.socket;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomSubscriptionService {

    private final ConcurrentHashMap<String, Set<Long>> roomSubscriptions = new ConcurrentHashMap<>();

    public void addUserToRoom(String idRoom, Long idUser) {
        roomSubscriptions.computeIfAbsent(idRoom, k -> ConcurrentHashMap.newKeySet()).add(idUser);
    }

    public void removeUserFromRoom(String idRoom, Long idUser) {
        Set<Long> users = roomSubscriptions.get(idRoom);
        if (users != null) {
            users.remove(idUser);
            if (users.isEmpty()) {
                roomSubscriptions.remove(idRoom);
            }
        }
    }

    public Set<Long> getUsersInRoom(String idRoom) {
        return roomSubscriptions.getOrDefault(idRoom, ConcurrentHashMap.newKeySet());
    }

    public String removeUserFromAnyRooms(Long idUser) {
        for(Map.Entry<String, Set<Long>> room :roomSubscriptions.entrySet()) {
            String idRoom = room.getKey();
            Set<Long> users = room.getValue();
            if(users.contains(idUser)) {
                users.remove(idUser);
                if(users.isEmpty()){
                    roomSubscriptions.remove(idRoom);
                }
                return idRoom;
            }
        }
        return null;
    }
}