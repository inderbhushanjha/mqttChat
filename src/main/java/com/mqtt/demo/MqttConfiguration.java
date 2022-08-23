package com.mqtt.demo;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.Objects;

@Configuration
public class MqttConfiguration {

    // logic for subscribing
    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[] {"ssl://dropstat-cgvibz6nuikq.cedalo.cloud:8883"});
        options.setUserName("dropstat");
        options.setPassword("Dropstat@2022".toCharArray());
        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;

    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("dropstat", mqttPahoClientFactory(), "chat-inder");
        adapter.setCompletionTimeout(50000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(){
        return message -> {
            String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();

            System.out.println(topic);
            System.out.println(message.getPayload());
            System.out.println(message.getHeaders().get("mqtt_receivedTopic"));
        };
    }

    // outbound for publishing

    @Bean
    public MessageChannel mqttOutboundChannel(){
        return new DirectChannel();
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(){
        MqttPahoMessageHandler mqttPahoMessageHandler = new MqttPahoMessageHandler("dropstat", mqttPahoClientFactory());

        // for always listening
        mqttPahoMessageHandler.setAsync(true);

        mqttPahoMessageHandler.setDefaultTopic("chat-inder");
        mqttPahoMessageHandler.setDefaultRetained(false);
        return mqttPahoMessageHandler;
    }
}
