package com.example.reservationmicroservice.dto;

import com.example.reservationmicroservice.event.EventType;
import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.service.ReservationService;
import lombok.Data;

@Data
public class ReservationMessage {

    private long userId;

    private long accommodationId;

    private EventType type;

    public ReservationMessage(long userId, long accommodationId, EventType type) {
        this.userId = userId;
        this.accommodationId = accommodationId;
        this.type = type;
    }

    public ReservationMessage() {}
}
