package com.elp.compliance_manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic connectorSyncRequestsTopic() {
        return TopicBuilder.name("connector-sync-requests")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic connectorSyncCompletedTopic() {
        return TopicBuilder.name("connector-sync-completed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic complianceRunRequestsTopic() {
        return TopicBuilder.name("compliance-run-requests")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name("audit-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
