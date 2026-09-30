package com.example.hclai.drools_play_ground.service;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import com.example.hclai.drools_play_ground.model.GuestData;
import com.example.hclai.drools_play_ground.model.GuestResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountService {

    private final KieContainer kieContainer;

    public GuestResult fireRule(GuestData data) {

        KieSession kieSession = kieContainer.newKieSession("discountSession");

        GuestResult result = new GuestResult();
        result.setName(data.getName());
        result.setResult(false);
        result.setFinalPrice(data.getPrice());
        result.setKickout(false);

        try {

             kieSession.getKieBase().getKiePackages().forEach(kpkg ->
                kpkg.getRules().forEach(rule -> log.info("LOADED RULE: package={}, name={}", kpkg.getName(), rule.getName()) )
            );

            kieSession.addEventListener(new DefaultAgendaEventListener() {

                @Override
                public void matchCreated(MatchCreatedEvent event) {
                    log.info("MATCH CREATED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void matchCancelled(MatchCancelledEvent event) {
                    log.info("MATCH CANCELLED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void afterMatchFired(AfterMatchFiredEvent event) {
                    log.info("FIRED: {}", event.getMatch().getRule().getName());
                }

            });

            kieSession.insert(data);
            kieSession.insert(result);

            kieSession.fireAllRules();

        } finally {
            kieSession.dispose();
        }

        return result;

    }




}
