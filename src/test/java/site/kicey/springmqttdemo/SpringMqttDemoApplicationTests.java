package site.kicey.springmqttdemo;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@SpringBootTest
class SpringMqttDemoApplicationTests {
    
    @Test
    void contextLoads(@Autowired MqttPahoClientFactory mqttClientFactory) throws MqttException {
        assert mqttClientFactory != null;
        var client1 = mqttClientFactory.getClientInstance("tcp://kicey.site:1883", "test");
        var client2 = mqttClientFactory.getAsyncClientInstance("tcp://kicey.site:1883", "test");
    }
    
}
