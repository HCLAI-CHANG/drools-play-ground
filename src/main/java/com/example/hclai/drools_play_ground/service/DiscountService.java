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

        // Step 1: 給 kieContainer 指定的 session name 以取得 kieSession
        KieSession kieSession = kieContainer.newKieSession("discountSession");

        // Step 2: 初始化 result fact
        GuestResult result = new GuestResult();
        result.setName(data.getName());
        result.setResult(false);
        result.setFinalPrice(data.getPrice());
        result.setKickout(false);

        try {

            // 設定監聽器，與邏輯無關
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

            // Step 3: 將 request fact 及 result fact 都放入 kieSession
            kieSession.insert(data);
            kieSession.insert(result);

            // Step 4: 執行規則判斷
            kieSession.fireAllRules();

        } finally {

            // Step 5: 釋放資源
            kieSession.dispose();
        }

        // Step 6: 此時 result fact 已被更新，回傳給 controllser
        return result;

    }

}
