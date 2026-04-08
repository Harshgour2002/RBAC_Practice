package com.example.rbac.config;

import com.example.rbac.model.RolePermissionEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic rbacEventsTopic() {
        return TopicBuilder.name("rbac.events").partitions(12).replicas(1).build();
    }

    @Bean
    public Class<RolePermissionEvent> rolePermissionEventClass() {
        return RolePermissionEvent.class;
    }
}
