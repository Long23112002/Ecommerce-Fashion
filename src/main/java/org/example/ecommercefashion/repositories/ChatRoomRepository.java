package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.ChatRoom;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    @Aggregation(pipeline = {
            "{ '$lookup': { 'from': 'chat', 'localField': '_id', 'foreignField': 'id_room', 'as': 'chat' }}",
            "{ '$match': { '$expr': { '$gt': [{ $size: '$chat' }, 0] }, 'deleted': false} }",
            "{ '$project': { '_id': 1, 'id_client': 1, 'create_at': 1, 'deleted': 1, 'chat': {'$arrayElemAt': ['$chat', -1]} } }",
            "{ '$sort': { 'chat.seen': 1, 'chat.create_at': -1 }}"
    })
    List<ChatRoom> findAllChatRoom();

    @Query(value = "{ 'id_client' : :#{#id} }")
    Optional<ChatRoom> findChatRoomByUserId(@Param("id") Long id);

}
