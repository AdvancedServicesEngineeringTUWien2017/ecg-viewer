package at.ase.webapp.config;

import at.ase.webapp.web.rabbitmq.HeartListener;
import at.ase.webapp.web.rabbitmq.Listener;
import at.ase.webapp.web.rabbitmq.dto.ECGDataDTO;
import at.ase.webapp.web.rabbitmq.dto.HeartRateDataDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by gena on 05/06/17.
 */
@Configuration
public class RabbitMQConfig {
    final static String queueName = "ecg_data_webapp";
    final static String routingKey = "ecg_data_route";
    final static String heart_routingKey = "heart_ecg_data_route";
    final static String heartRateQueueName = "ecg_heart_rate";

    @Bean
    Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    Queue heartQueue() {
        return new Queue(heartRateQueueName, false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("ecg_data_exchange_direct");
    }

    @Bean
    DirectExchange heartExchange() {
        return new DirectExchange("heart_data_exchange_direct");
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Binding heartBinding(Queue heartQueue, DirectExchange heartExchange) {
        return BindingBuilder.bind(heartQueue).to(heartExchange).with(heart_routingKey);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    @Bean
    SimpleMessageListenerContainer heartContainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter heartListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(heartRateQueueName);
        container.setMessageListener(heartListenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter heartListenerAdapter(HeartListener heartReceiver) {
        return new MessageListenerAdapter(heartReceiver, new MessageConverter() {
            ObjectMapper mapper = new ObjectMapper();
            @Override
            public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
                return null;
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                try {
                    return mapper.readValue(message.getBody(), HeartRateDataDTO.class);
                } catch (IOException e) {
                    throw new MessageConversionException("damke",e);
                }
            }
        });
    }


    @Bean
    MessageListenerAdapter listenerAdapter( Listener receiver) {
        return new MessageListenerAdapter(receiver, new MessageConverter() {
            ObjectMapper mapper = new ObjectMapper();
            @Override
            public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
                return null;
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                try {
                    return mapper.readValue(message.getBody(), ECGDataDTO.class);
                } catch (IOException e) {
                    throw new MessageConversionException("damke",e);
                }
            }
        });
    }
}
