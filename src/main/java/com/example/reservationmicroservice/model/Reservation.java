package com.example.reservationmicroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Reservation {
    @Id
    private String id;

    @Field
    private LocalDate start;

    @Field
    private LocalDate end;

    @Field
    private Long userId;

    @Field
    private ReservationStatus status;

    @Field
    private String slotId;

    @Field
    private int numberOfGuests;
}
