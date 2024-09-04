package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    @Query(value = "{ 'id_room' : :#{#id} }")
    List<Chat> findAllChatByIdChatRoom(@Param("id") String id);
}
