package com.example.rbac.events;

import com.example.rbac.model.RolePermissionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RbacEventPublisher {
    private final KafkaTemplate<String, RolePermissionEvent> kafkaTemplate;

    public RbacEventPublisher(KafkaTemplate<String, RolePermissionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RolePermissionEvent event) {
        kafkaTemplate.send("rbac.events", event.getUserId().toString(), event);
    }
}
