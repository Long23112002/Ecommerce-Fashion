package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.NotificationMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationMessageRepository extends MongoRepository<NotificationMessage, Long> {}
