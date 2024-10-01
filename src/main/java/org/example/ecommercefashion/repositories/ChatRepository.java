package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.entities.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    @Aggregation(pipeline = {
            "{ $match: { 'id_room' : :#{#id} } }",
            "{ $sort: { 'create_at' : -1 } }",
            "{ $skip: :#{#offset} }",
            "{ $limit: :#{#limit} }",
            "{ $sort: { 'create_at' : 1 } }"
    })
    List<Chat> findAllChatByIdChatRoom(String id, int offset, int limit);

    @Aggregation(pipeline = {
            "{ $match: { 'id_room' : :#{#id} } }",
            "{ $sort: { 'create_at' : -1 } }",
            "{ $limit: 1 }"
    })
    Optional<Chat> findLastChatByIdChatRoom(String id);

    @Aggregation(pipeline = {
            "{ $match: { id_room: { $in: :#{#ids} } } }",
            "{ $group: { _id: '$id_room', doc: { $last: '$$ROOT' } } }",
            "{ $replaceRoot: { newRoot: '$doc' } }"
    })
    List<Chat> findAllLastChatByIdRooms(Collection<String> ids);

    @Aggregation(pipeline = {
            "{ $match: { 'id_room': :#{#targetChat.idRoom}, 'create_at': { $gte: :#{#targetChat.createAt} } } }",
            "{ $sort: { 'create_at' : 1 } }"
    })
    List<Chat> findChatsUntilTarget(Chat targetChat);


    @Aggregation(pipeline = {
            "{$match: {_id: {$in: :#{#ids}}}}"
    })
    List<Chat> findAllByIds(Collection<String> ids);

    int countByIdRoom(String id);
}
