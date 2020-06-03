/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sample.demo.service;

import com.sample.demo.model.IcndbJoke;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author YAR
 */
@Slf4j
@Service
public class PushChuckNorrisService {

    private final FCMClient fcmClient;

//    private final WebClient webClient;

    private int seq = 0;

    public PushChuckNorrisService(FCMClient fcmClient
//            , WebClient webClient
    ) {
        this.fcmClient = fcmClient;
//        this.webClient = webClient;
    }

    @Scheduled(fixedDelay = 30_000)
    public void sendChuckQuotes() {
        IcndbJoke joke = WebClient.create().get().uri("http://api.icndb.com/jokes/random")
                .retrieve().bodyToMono(IcndbJoke.class).block();
        try {
            sendPushMessage(joke);
        } catch (InterruptedException | ExecutionException e) {
            log.error("send chuck joke", e);
        }
    }

    void sendPushMessage(IcndbJoke joke) throws InterruptedException, ExecutionException {
        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(joke.getValue().getId()));
        data.put("joke", joke.getValue().getJoke());
        data.put("seq", String.valueOf(this.seq++));
        data.put("ts", String.valueOf(System.currentTimeMillis()));

        System.out.println("Sending chuck joke...");
        this.fcmClient.send(data);
    }
}
