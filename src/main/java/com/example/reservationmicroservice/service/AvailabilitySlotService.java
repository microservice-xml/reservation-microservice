package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.exception.AvailabilitySlotException;
import com.example.reservationmicroservice.model.AvailabilitySlot;
import com.example.reservationmicroservice.repository.AvailabilitySlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public void add(AvailabilitySlot availabilitySlot) {
        isDateRangeValidForAdd(availabilitySlot);
        availabilitySlotRepository.save(availabilitySlot);
    }

    private void isDateRangeValidForAdd(AvailabilitySlot availabilitySlot) {
        var availabilitySlots = availabilitySlotRepository.findAll();
        for (AvailabilitySlot as: availabilitySlots) {
            if (areDatesOverlapping(availabilitySlot, as)) {
                throw new AvailabilitySlotException("Date range of the slot you want to create overlaps with an existing one!");
            }
        }
    }

    private boolean areDatesOverlapping(AvailabilitySlot availabilitySlot, AvailabilitySlot as) {
        return !((availabilitySlot.getStart().isBefore(as.getStart()) && availabilitySlot.getEnd().isBefore(as.getStart()))
                || (availabilitySlot.getStart().isAfter(as.getEnd()) && availabilitySlot.getEnd().isAfter(as.getEnd())));
    }

    public void edit(AvailabilitySlot availabilitySlot) {
        isDateRangeValidForEdit(availabilitySlot);
        var oldAvailabilitySlot = availabilitySlotRepository.findById(availabilitySlot.getId()).orElseThrow(() -> new AvailabilitySlotException("Slot with this id doesn't exist"));
        oldAvailabilitySlot.setPrice(availabilitySlot.getPrice());
        oldAvailabilitySlot.setStart(availabilitySlot.getStart());
        oldAvailabilitySlot.setEnd(availabilitySlot.getEnd());
        availabilitySlotRepository.save(oldAvailabilitySlot);
    }

    private void isDateRangeValidForEdit(AvailabilitySlot availabilitySlot) {
        var availabilitySlots = availabilitySlotRepository.findAll();
        for (AvailabilitySlot as: availabilitySlots) {
            if (areDatesOverlapping(availabilitySlot, as) && !as.getId().equals(availabilitySlot.getId())) {
                throw new AvailabilitySlotException("Date range of the slot you want to create overlaps with an existing one!");
            }
        }
    }
}
