/*
 * Copyright (C) 2025 Leetjourney
 * Licensed under the CC BY-NC 4.0 License.
 * See LICENSE file for details.
 */

package com.leetjourney.notificationservice.service;

import com.leetjourney.asteroidalerting.event.AsteroidCollisionEvent;
import com.leetjourney.notificationservice.entity.Notification;
import com.leetjourney.notificationservice.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "asteroid-alert", groupId = "notification-service")
    public void alertEvent(AsteroidCollisionEvent notificationEvent) {
        log.info("Received asteroid alerting event: {}", notificationEvent);

        // Create entity for notification
        final Notification notification = Notification.builder()
                .asteroidName(notificationEvent.getAsteroidName())
                .closeApproachDate(LocalDate.parse(notificationEvent.getCloseApproachDate()))
                .estimatedDiameterAvgMeters(notificationEvent.getEstimatedDiameterAvgMeters())
                .missDistanceKilometers(new BigDecimal(notificationEvent.getMissDistanceKilometers()))
                .emailSent(false)
                .build();

        // Save notification
        final Notification savedNotification = notificationRepository.saveAndFlush(notification);
        log.info("Notification saved: {}", savedNotification);
    }

    @Scheduled(fixedRate = 10000)
    public void sendAlertingEmail() {
        log.info("Triggering scheduled job to send email alerts");
        emailService.sendAsteroidAlertEmail();
    }
}
