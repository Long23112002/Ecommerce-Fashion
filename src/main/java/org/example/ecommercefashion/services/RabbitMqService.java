package org.example.ecommercefashion.services;

public interface RabbitMqService {

  <T> void sendMessage(String queueName, T message, Class<T> clazz);

  //    void receiveMessage(byte[] message);
}
