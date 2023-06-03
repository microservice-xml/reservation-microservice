package com.example.reservationmicroservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class SlotsDeleted extends BaseEvent {

    public SlotsDeleted(LocalDateTime timestamp, EventType type) {
        super(timestamp, type);
    }

    public SlotsDeleted() {
    }
}
