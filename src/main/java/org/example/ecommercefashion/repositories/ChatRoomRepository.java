package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    @Query(value = "{ 'id_client' : :#{#id} }")
    Optional<ChatRoom> findChatRoomByUserId(@Param("id") Long id);

}
