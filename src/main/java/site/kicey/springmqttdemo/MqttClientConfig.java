package site.kicey.springmqttdemo;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * @author Kicey
 */
@Configuration
public class MqttClientConfig {
    
    /**
     * @return mqtt 消息输入管道
     */
    @Bean
    public MessageChannel mqttInputChannel(){
        return new PublishSubscribeChannel();
    }
    
    /**
     * @return mqtt 消息输出管道
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new PublishSubscribeChannel();
    }
    
    /**
     * @return mqtt 客户端工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        IMqttPahoClientFactory factory = new IMqttPahoClientFactory();
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { "tcp://kicey.site:1883"});
        options.setUserName("test");
        options.setPassword("test".toCharArray());
        
        factory.setConnectionOptions(options);
        return factory;
    }
    
    /**
     * @return 消息生产者，通过 Paho mqtt 客户端接收消息，将消息转化为 spring-integration 消息
     */
    @Bean
    public MessageProducer inbound(){
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "spring-client",
                mqttClientFactory(),
                "spring-topic-in");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        // 指定消息输入管道
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    
    /**
     * @return 消息的处理者，使用 inputChannel 指定从哪个管道获取 mqtt 消息
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> System.out.println(message.getPayload());
    }
    
    /**
     * 消息的消费者，负责将从管道收到的 spring-integration 消息转化为 mqtt 消息发送
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("spring-client", mqttClientFactory());
        
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(0);
        messageHandler.setDefaultTopic("spring-topic-out");
        
        return messageHandler;
    }
    
    /**
     * 定义接口，由容器完成实际 bean 的装配，负责将调用转化为消息发送到管道
     */
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface HttpToMqttProxy {
        
        void sendToMqtt(String data);
        
    }
}
