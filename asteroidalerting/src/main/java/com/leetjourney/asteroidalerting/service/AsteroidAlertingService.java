/*
 * Copyright (C) 2025 Leetjourney
 * Licensed under the CC BY-NC 4.0 License.
 * See LICENSE file for details.
 */

package com.leetjourney.asteroidalerting.service;

import com.leetjourney.asteroidalerting.client.NasaClient;
import com.leetjourney.asteroidalerting.dto.Asteroid;
import com.leetjourney.asteroidalerting.event.AsteroidCollisionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AsteroidAlertingService {

    private final NasaClient nasaClient;
    private final KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate;

    @Autowired
    public AsteroidAlertingService(NasaClient nasaClient, KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate) {
        this.nasaClient = nasaClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void alert() {
        log.info("Alerting service called");

        // From and to date
        final LocalDate fromDate = LocalDate.now();
        final LocalDate toDate = LocalDate.now().plusDays(7);

        // Call NASA API to get the asteroid data
        log.info("Getting asteroid list for dates: {} to {}", fromDate, toDate);
        final List<Asteroid> asteroidList =  nasaClient.getNeoAsteroids(fromDate, toDate);
        log.info("Retrieved Asteroid list of size: {}", asteroidList.size());

        // If there are any hazardous asteroids, send an alert
        final List<Asteroid> dangerousAsteroids = asteroidList.stream()
                .filter(Asteroid::isPotentiallyHazardous)
                .toList();
        log.info("Found {} hazardous asteroids", dangerousAsteroids.size());
        // Create an alert and put on Kafka topic
        final List<AsteroidCollisionEvent> asteroidCollisionEventList =
                createEventLisOfDangerousAsteroids(dangerousAsteroids);

        log.info("Sending {} asteroid alerts to Kafka", asteroidCollisionEventList.size());
        asteroidCollisionEventList.forEach(event -> {
            kafkaTemplate.send("asteroid-alert", event);
            log.info("Asteroid alert sent to Kafka topic: {}", event);
        });
    }

    private List<AsteroidCollisionEvent> createEventLisOfDangerousAsteroids(final List<Asteroid> dangerousAsteroids) {
        return dangerousAsteroids.stream()
                .map(asteroid ->{
                    if (asteroid.isPotentiallyHazardous()) {
                        return AsteroidCollisionEvent.builder()
                                .asteroidName(asteroid.getName())
                                .closeApproachDate(asteroid.getCloseApproachData().getFirst().getCloseApproachDate().toString())
                                .missDistanceKilometers(asteroid.getCloseApproachData().getFirst().getMissDistance().getKilometers())
                                .estimatedDiameterAvgMeters((asteroid.getEstimatedDiameter().getMeters().getMinDiametre() +
                                        asteroid.getEstimatedDiameter().getMeters().getMaxDiametre()) / 2)
                                .build();
                    }
                    return null;
                })
                .toList();
    }
}
