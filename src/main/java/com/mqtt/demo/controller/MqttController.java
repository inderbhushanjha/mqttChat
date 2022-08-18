package com.mqtt.demo.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mqtt.demo.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttController {

    @Autowired
    MqttGateway mqttGateway;

    @PostMapping("/chat")
    public ResponseEntity<?> publish(@RequestBody String mqttMessage){
        try{
            JsonObject jsonObject = new Gson().fromJson(mqttMessage, JsonObject.class);
            mqttGateway.send(jsonObject.get("message").toString(), jsonObject.get("topic").toString());
            return ResponseEntity.ok("success");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok("fail");
        }
    }
}
