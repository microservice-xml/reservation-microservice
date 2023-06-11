package com.example.reservationmicroservice.event;

public enum EventType {

    DELETE_USER_STARTED, DELETE_ACCOMMODATION_STARTED, DELETE_ACCOMMODATION_FAILED, DELETE_SLOT_FAILED,
    ACCOMMODATION_DELETED, SLOTS_DELETED, ACCOMMODATION_DELETE_FAILED, SLOTS_DELETE_FAILED,

    USER_STAYED, RESERVATION_CANCELED
}
