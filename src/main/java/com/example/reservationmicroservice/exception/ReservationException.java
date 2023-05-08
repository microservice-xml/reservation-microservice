package com.example.reservationmicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ReservationException extends RuntimeException{
    public ReservationException(String message){
        super(message);
    }
}
