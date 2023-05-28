package com.example.reservationmicroservice.mapper;

import communication.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.reservationmicroservice.mapper.LocalDateMapper.convertGoogleTimestampToLocalDate;
import static com.example.reservationmicroservice.mapper.LocalDateMapper.convertLocalDateToGoogleTimestamp;
import static com.example.reservationmicroservice.mapper.ReservationStatusMapper.convertReservationStatusToReservationStatusGrpc;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    public static com.example.reservationmicroservice.model.Reservation convertReservationGrpcToReservation(Reservation reservation) {
        return com.example.reservationmicroservice.model.Reservation.builder()
                .start(convertGoogleTimestampToLocalDate(reservation.getStart()))
                .end(convertGoogleTimestampToLocalDate(reservation.getEnd()))
                .userId(reservation.getUserId())
                .slotId(reservation.getSlotId())
                .numberOfGuests(reservation.getNumberOfGuests())
                .hostId(reservation.getHostId())
                .build();
    }

    public static Reservation convertReservationToReservationGrpc(com.example.reservationmicroservice.model.Reservation reservation){
        return Reservation.newBuilder()
                .setId(reservation.getId())
                .setUserId(reservation.getUserId())
                .setSlotId(reservation.getSlotId())
                .setNumberOfGuests(reservation.getNumberOfGuests())
                .setStart(convertLocalDateToGoogleTimestamp(reservation.getStart()))
                .setEnd(convertLocalDateToGoogleTimestamp(reservation.getEnd()))
                .setStatus(convertReservationStatusToReservationStatusGrpc(reservation.getStatus()))
                .setHostId(reservation.getHostId())
                .build();
    }
}
