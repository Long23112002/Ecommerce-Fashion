package org.example.ecommercefashion.config.rabitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  public static final String MY_QUEUE = "testQueue";
  public static final String MY_EXCHANGE = "testExchange";

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory("160.30.44.139", 5672);
    factory.setUsername("root");
    factory.setPassword("12345678");
    return factory;
  }

  @Bean
  public Queue myQueue() {
    return new Queue(MY_QUEUE, true);
  }

  @Bean
  public TopicExchange myExchange() {
    System.out.println("Creating exchange: " + MY_EXCHANGE);
    return new TopicExchange(MY_EXCHANGE);
  }

//  @Bean
//  public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//    rabbitAdmin.initialize();
//    rabbitAdmin.declareQueue(myQueue());
//    rabbitAdmin.declareExchange(myExchange());
//
//    return rabbitAdmin;
//  }
}
