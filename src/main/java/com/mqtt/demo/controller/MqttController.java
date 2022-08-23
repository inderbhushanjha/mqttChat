package com.mqtt.demo.controller;

import com.mqtt.demo.MqttGateway;
import com.mqtt.demo.vo.MessageRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MqttController {

    @Autowired
    MqttGateway mqttGateway;

    @PostMapping("/chat")
    public ResponseEntity<?> publish(@RequestBody MessageRequestVO message){
        try{
            mqttGateway.send(message.getTopic(), message.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("message fail to send", HttpStatus.REQUEST_TIMEOUT);
        }
        return new  ResponseEntity<>("message sent", HttpStatus.OK);
    }
}
