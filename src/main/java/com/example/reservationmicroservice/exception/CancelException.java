package com.example.reservationmicroservice.exception;

public class CancelException extends Exception{
    public CancelException(){
        super("You can't cancel your reservation now, there's less than a day left.");
    }
}
