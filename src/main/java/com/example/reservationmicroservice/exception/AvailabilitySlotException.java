package com.example.reservationmicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AvailabilitySlotException extends RuntimeException {

    public AvailabilitySlotException(String message) {
        super(message);
    }

}