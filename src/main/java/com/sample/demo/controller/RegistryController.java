/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sample.demo.controller;

import com.sample.demo.service.FCMClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 *
 * @author YAR
 */
@RestController
public class RegistryController {

    private final FCMClient fcmClient;

    public RegistryController(FCMClient fcmClient) {
        this.fcmClient = fcmClient;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> register(@RequestBody Mono<String> token) {
        return token.doOnNext(t -> this.fcmClient.subscribe("chuck", t)).then();
    }
}
