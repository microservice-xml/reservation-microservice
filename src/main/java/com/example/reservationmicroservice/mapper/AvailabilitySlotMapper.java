package com.example.reservationmicroservice.mapper;

import com.example.reservationmicroservice.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.reservationmicroservice.mapper.ReservationMapper.convertReservationGrpcToReservation;
import static com.example.reservationmicroservice.mapper.ReservationMapper.convertReservationToReservationGrpc;

@Component
@RequiredArgsConstructor
public class AvailabilitySlotMapper {
    public static communication.AvailabilitySlotFull convertAvailabilitySlotToAvailabilitySlotGrpc(com.example.reservationmicroservice.model.AvailabilitySlot availabilitySlot){
        return communication.AvailabilitySlotFull.newBuilder()
                .setId(availabilitySlot.getId())
                .setPrice(availabilitySlot.getPrice())
                .setAccommodationId(availabilitySlot.getAccommodationId())
                .addAllReservations(convertReservationListToReservationGrpcList(availabilitySlot.getReservations()))
                .setStartYear(availabilitySlot.getStart().getYear())
                .setStartMonth(availabilitySlot.getStart().getMonthValue())
                .setStartDay(availabilitySlot.getStart().getDayOfMonth())
                .setEndYear(availabilitySlot.getEnd().getYear())
                .setEndMonth(availabilitySlot.getEnd().getMonthValue())
                .setEndDay(availabilitySlot.getEnd().getDayOfMonth())
                .build();
    }

    public static List<communication.Reservation> convertReservationListToReservationGrpcList(List<Reservation> reservationList) {
        List<communication.Reservation> retVal = new ArrayList<>();
        for (Reservation res: reservationList) {
            retVal.add(convertReservationToReservationGrpc(res));
        }
        return retVal;
    }

    public static com.example.reservationmicroservice.model.AvailabilitySlot convertAvailabilitySlotGrpcToAvailabilitySlot(communication.AvailabilitySlotFull availabilitySlotFull){
        return com.example.reservationmicroservice.model.AvailabilitySlot.builder()
                .id(availabilitySlotFull.getId())
                .price(availabilitySlotFull.getPrice())
                .accommodationId(availabilitySlotFull.getAccommodationId())
                .reservations(convertReservationGrpcListToReservationList(availabilitySlotFull.getReservationsList()))
                .start(LocalDate.of(availabilitySlotFull.getStartYear(), availabilitySlotFull.getStartMonth(), availabilitySlotFull.getStartDay()))
                .end(LocalDate.of(availabilitySlotFull.getEndYear(), availabilitySlotFull.getEndMonth(), availabilitySlotFull.getEndDay()))
                .build();
    }

    public static List<Reservation> convertReservationGrpcListToReservationList(List<communication.Reservation> reservationList) {
        List<Reservation> retVal = new ArrayList<>();
        for (communication.Reservation res: reservationList) {
            retVal.add(convertReservationGrpcToReservation(res));
        }
        return retVal;
    }
}
