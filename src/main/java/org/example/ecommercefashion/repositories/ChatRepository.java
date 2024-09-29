package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.entities.Chat;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    @Aggregation(pipeline = {
            "{ $match: { 'id_room' : :#{#id} } }",
            "{ $sort: { 'create_at' : -1 } }",
            "{ $skip: :#{#offet} }",
            "{ $limit: :#{#limit} }",
            "{ $sort: { 'create_at' : 1 } }"
    })
    List<Chat> findAllChatByIdChatRoom(@Param("id") String id, int offet, int limit);

    @Aggregation(pipeline = {
            "{ $match: { 'id_room' : :#{#id} } }",
            "{ $sort: { 'create_at' : -1 } }",
            "{ $limit: 1 }"
    })
    Optional<Chat> findLastChatByIdChatRoom(@Param("id") String id);

    @Aggregation(pipeline = {
            "{ $match: { id_room: { $in: :#{#ids} } } }",
            "{ $group: { _id: '$id_room', doc: { $last: '$$ROOT' } } }",
            "{ $replaceRoot: { newRoot: '$doc' } }"
    })
    List<Chat> findAllLastChatByRoomIds(Collection<String> ids);

    @Aggregation(pipeline = {
            "{ $match: { 'id_room': :#{#target_chat.idRoom}, 'create_at': { $gte: :#{#target_chat.createAt} } } }",
            "{ $sort: { 'create_at' : 1 } }"
    })
    List<Chat> findChatsUntilTarget(@Param("target_chat") Chat targetChat);

}
