package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.exception.AvailabilitySlotException;
import com.example.reservationmicroservice.model.Accommodation;
import com.example.reservationmicroservice.model.AvailabilitySlot;
import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.repository.AvailabilitySlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public void add(AvailabilitySlot availabilitySlot) {
        isDateRangeValidForAdd(availabilitySlot);
        availabilitySlotRepository.save(availabilitySlot);
    }

    private void isDateRangeValidForAdd(AvailabilitySlot availabilitySlot) {
        var availabilitySlots = availabilitySlotRepository.findByAccommodationId(availabilitySlot.getAccommodationId());
        for (AvailabilitySlot as : availabilitySlots) {
            if (areDatesOverlapping(availabilitySlot.getStart(), availabilitySlot.getEnd(), as.getStart(), as.getEnd()) && as.getAccommodationId() == availabilitySlot.getAccommodationId()) {
                throw new AvailabilitySlotException("Date range of the slot you want to create overlaps with an existing one!");
            }
        }
    }

    private boolean areDatesOverlapping(LocalDate start, LocalDate end, LocalDate start2, LocalDate end2) {
        return !((start.isBefore(start2) && end.isBefore(start2))
                || (start.isAfter(end2) && end.isAfter(end2)));
    }

    public void edit(AvailabilitySlot availabilitySlot) {
        isDateRangeValidForEdit(availabilitySlot);
        if (availabilitySlot.getReservations() != null && availabilitySlot.getReservations().size() != 0) {
            throw new AvailabilitySlotException("Can't edit availability slot that already has reservations");
        }
        var oldAvailabilitySlot = availabilitySlotRepository.findById(availabilitySlot.getId()).orElseThrow(() -> new AvailabilitySlotException("Slot with this id doesn't exist"));
        oldAvailabilitySlot.setPrice(availabilitySlot.getPrice());
        oldAvailabilitySlot.setStart(availabilitySlot.getStart());
        oldAvailabilitySlot.setEnd(availabilitySlot.getEnd());
        availabilitySlotRepository.save(oldAvailabilitySlot);
    }

    private void isDateRangeValidForEdit(AvailabilitySlot availabilitySlot) {
        var availabilitySlots = availabilitySlotRepository.findByAccommodationId(availabilitySlot.getAccommodationId());
        for (AvailabilitySlot as : availabilitySlots) {
            if (areDatesOverlapping(availabilitySlot.getStart(), availabilitySlot.getEnd(), as.getStart(), as.getEnd()) && !as.getId().equals(availabilitySlot.getId())) {
                throw new AvailabilitySlotException("Date range of the slot you want to create overlaps with an existing one!");
            }
        }
    }

    public List<Long> getByAvailabilityRange(List<Long> appointmentIds, LocalDate startDate, LocalDate endDate) {
        var availabilitySlots = availabilitySlotRepository.findAllByIdAndAvailabilityRange(appointmentIds, startDate, endDate);
        if (availabilitySlots.size() == 0) {
            throw new AvailabilitySlotException("There are no availability slots found");
        }
        ArrayList<Long> validAccommodation = getValidAccommodation(startDate, endDate, availabilitySlots);
        return validAccommodation.stream().distinct().toList();
    }

    private ArrayList<Long> getValidAccommodation(LocalDate startDate, LocalDate endDate, List<AvailabilitySlot> availabilitySlots) {
        var validAccommodation = new ArrayList<Long>();
        for (AvailabilitySlot as : availabilitySlots) {
            var valid = true;
            for (Reservation res : as.getReservations()) {
                if (areDatesOverlapping(startDate, endDate, res.getStart(), res.getEnd())) {
                    valid = false;
                }
            }
            if (valid) validAccommodation.add(as.getAccommodationId());
        }
        return validAccommodation;
    }

    public List<AvailabilitySlot> getAllByAccommodationId(Long accommodationId) {
        var availabilitySlots = availabilitySlotRepository.findByAccommodationId(accommodationId);
        if (availabilitySlots.size() == 0) {
            throw new AvailabilitySlotException("There are not availability slots for this accommodation!");
        }
        return availabilitySlots;
    }
}
