package com.example.ecommerce.monitoring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "custom")
public class CustomActuator {

    @ReadOperation
    public Map<String, String> Custom(String memory) {
        Map<String, String> map = new HashMap<>();
        map.put("memory", memory);
        map.put("storage", "20Gb");
        return map;
    }
}
