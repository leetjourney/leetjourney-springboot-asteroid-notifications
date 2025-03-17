package com.leetjourney.asteroidalerting.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsteroidCollisionEvent {
    private String asteroidName;
    private String closeApproachDate;
    private String missDistanceKilometers;
    private double estimatedDiameterAvgMeters;
}
