/*
 * Copyright (C) 2025 Leetjourney
 * Licensed under the CC BY-NC 4.0 License.
 * See LICENSE file for details.
 */

package com.leetjourney.asteroidalerting.controller;

import com.leetjourney.asteroidalerting.service.AsteroidAlertingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/asteroid-alerting")
public class AsteroidAlertingController {

    private final AsteroidAlertingService asteroidAlertingService;

    @Autowired
    public AsteroidAlertingController(AsteroidAlertingService asteroidAlertingService) {
        this.asteroidAlertingService = asteroidAlertingService;
    }

    @PostMapping("/alert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void alert() {
        asteroidAlertingService.alert();
    }
}
