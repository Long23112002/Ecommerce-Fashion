package org.example.ecommercefashion.config.socket.notification;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationSubscriptionService {

    private final Set<Long> notificationSubscriptions = ConcurrentHashMap.newKeySet();

    public Set<Long> getAllSubscribedUsers() {
        return this.notificationSubscriptions;
    }

    public void addUser(Long id) {
        this.notificationSubscriptions.add(id);
    }

    public void clearAll() {
        this.notificationSubscriptions.clear();
    }

    public void removeUser(Long id) {
        this.notificationSubscriptions.remove(id);
    }

    public boolean contains(Long id){
        return this.notificationSubscriptions.contains(id);
    }
}
