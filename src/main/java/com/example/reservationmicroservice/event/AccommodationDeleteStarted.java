package com.example.reservationmicroservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccommodationDeleteStarted extends BaseEvent {

    List<Long> accommodationIds;

    public AccommodationDeleteStarted(LocalDateTime timestamp, EventType type, List<Long> accommodationIds) {
        super(timestamp, type);
        this.accommodationIds = accommodationIds;
    }

    public AccommodationDeleteStarted(List<Long> accommodationIds) {
        this.accommodationIds = accommodationIds;
    }
}
