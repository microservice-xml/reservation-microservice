package com.example.reservationmicroservice.mapper;

import communication.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationStatusMapper {
    public static com.example.reservationmicroservice.model.ReservationStatus convertReservationStatusGrpcToReservationStatus(ReservationStatus status) {
        if (status.equals(ReservationStatus.ACCEPTED))
            return com.example.reservationmicroservice.model.ReservationStatus.ACCEPTED;
        else if (status.equals(ReservationStatus.PENDING))
            return com.example.reservationmicroservice.model.ReservationStatus.PENDING;
        else if (status.equals(ReservationStatus.DECLINED))
            return com.example.reservationmicroservice.model.ReservationStatus.DECLINED;
        else return com.example.reservationmicroservice.model.ReservationStatus.CANCELED;

    }

    public static ReservationStatus convertReservationStatusToReservationStatusGrpc(com.example.reservationmicroservice.model.ReservationStatus status) {
        if (status.equals(com.example.reservationmicroservice.model.ReservationStatus.ACCEPTED))
            return ReservationStatus.ACCEPTED;
        else if (status.equals(com.example.reservationmicroservice.model.ReservationStatus.PENDING))
            return ReservationStatus.PENDING;
        else if (status.equals(com.example.reservationmicroservice.model.ReservationStatus.DECLINED))
            return ReservationStatus.DECLINED;
        else
            return ReservationStatus.CANCELED;

    }
}
