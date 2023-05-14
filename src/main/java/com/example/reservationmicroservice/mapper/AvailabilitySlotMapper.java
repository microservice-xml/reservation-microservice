package com.example.reservationmicroservice.mapper;

import com.example.reservationmicroservice.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.reservationmicroservice.mapper.ReservationMapper.convertReservationToReservationGrpc;

@Component
@RequiredArgsConstructor
public class AvailabilitySlotMapper {
    public static communication.AvailabilitySlotFull convertAvailabilitySlotToAvailabilitySlotGrpc(com.example.reservationmicroservice.model.AvailabilitySlot availabilitySlot){
        return communication.AvailabilitySlotFull.newBuilder()
                .setId(availabilitySlot.getId())
                .setPrice(availabilitySlot.getPrice())
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
}
