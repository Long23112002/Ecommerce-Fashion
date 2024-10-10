package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Notification;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    @Aggregation(pipeline = {
            "{ $match: { 'id_receiver' : :#{#id}, 'deleted': false } }",
            "{ $sort: { 'create_at' : -1 } }",
            "{ $skip: :#{#offset} }",
            "{ $limit: :#{#limit} }"
    })
    List<Notification> findAllNotificationsByUserId(Long id, int offset, int limit);

    @Aggregation(pipeline = {
            "{ $match: { 'id_receiver' : :#{#id}, 'seen': false, 'deleted': false } }",
            "{ $sort: { 'create_at' : -1 } }",
            "{ $skip: :#{#offset} }",
            "{ $limit: :#{#limit} }"
    })
    List<Notification> findAllUnSeenNotificationByIdUser(Long id, Integer offset, Integer limit);

    int countByIdReceiverAndSeenIsFalseAndDeletedIsFalse(Long id);

    int countByIdReceiverAndDeletedIsFalse(Long id);

}
