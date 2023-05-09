package org.banka1.exchangeservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.forex.name}")
    private String forexQueue;

    @Value("${rabbitmq.queue.stock.name}")
    private String stockQueue;

    @Value("${rabbitmq.routing.forex.key}")
    private String forexRoutingKey;

    @Value("${rabbitmq.routing.stock.key}")
    private String stockRoutingKey;

    @Bean
    public Queue forexQueue(){
        return new Queue(forexQueue);
    }

    @Bean
    public Queue stockQueue(){
        return new Queue(stockQueue);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding forexBinding(){
        return BindingBuilder
                .bind(forexQueue())
                .to(exchange())
                .with(forexRoutingKey);
    }

    @Bean
    public Binding stockBinding(){
        return BindingBuilder
                .bind(stockQueue())
                .to(exchange())
                .with(stockRoutingKey);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
