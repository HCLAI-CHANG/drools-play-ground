package com.example.hclai.drools_play_ground.config;

import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() {

        KieServices kieServices = KieServices.Factory.get();
        KieContainer container = kieServices.getKieClasspathContainer();
        Results results = container.verify();
        results.getMessages(Message.Level.ERROR).forEach(msg -> log.error("Drools ERROR: {}", msg.getText()));

        return container;

    }

}
