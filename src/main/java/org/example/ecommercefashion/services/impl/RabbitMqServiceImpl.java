package org.example.ecommercefashion.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.example.ecommercefashion.services.RabbitMqService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqServiceImpl implements RabbitMqService {

  private final RabbitTemplate rabbitTemplate;

  public RabbitMqServiceImpl(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public <T> void sendMessage(String queueName, T message, Class<T> clazz) {
    try {
      byte[] serializedMessage = serializeAvro(message, clazz);
      rabbitTemplate.convertAndSend(queueName, serializedMessage);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to send message", e);
    }
  }

  //  @Override
  //  @RabbitListener(queues = "#{queueName}")
  //  public void receiveMessage(byte[] message) {
  //    try {
  //      Class<?> clazz = User.class;
  //      Object deserializedMessage = deserializeAvro(message, clazz);
  //      System.out.println("Received message: " + deserializedMessage);
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //      throw new RuntimeException("Failed to receive message", e);
  //    }
  //  }

  private <T> byte[] serializeAvro(T data, Class<T> clazz) throws IOException {
    DatumWriter<T> writer = new SpecificDatumWriter<>(clazz);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
    writer.write(data, encoder);
    encoder.flush();
    outputStream.close();
    return outputStream.toByteArray();
  }

  private <T> T deserializeAvro(byte[] data, Class<T> clazz) throws IOException {
    DatumReader<T> reader = new SpecificDatumReader<>(clazz);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    return reader.read(null, DecoderFactory.get().binaryDecoder(inputStream, null));
  }
}
