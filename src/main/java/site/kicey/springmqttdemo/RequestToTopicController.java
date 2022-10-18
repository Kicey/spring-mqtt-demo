package site.kicey.springmqttdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kicey
 */
@RestController
@RequestMapping("/")
public class RequestToTopicController {
    
    MqttClientConfig.HttpToMqttProxy httpToMqttProxy;
    
    public RequestToTopicController(MqttClientConfig.HttpToMqttProxy httpToMqttProxy){
        this.httpToMqttProxy = httpToMqttProxy;
    }
    
    @GetMapping("/mqtt")
    public String queryToTopic(@RequestParam String message){
        httpToMqttProxy.sendToMqtt(message);
        return "Success!";
    } 
}
