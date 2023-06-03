package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.event.BaseEvent;
import com.example.reservationmicroservice.event.EventType;
import com.example.reservationmicroservice.event.SlotsDeleteFailed;
import com.example.reservationmicroservice.event.SlotsDeleted;
import com.example.reservationmicroservice.exception.AvailabilitySlotException;
import com.example.reservationmicroservice.model.AvailabilitySlot;
import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.dto.AccommodationSearchDto;
import com.example.reservationmicroservice.repository.AvailabilitySlotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    public void add(AvailabilitySlot availabilitySlot) {
        isDateRangeValidForAdd(availabilitySlot);
        availabilitySlot.setId(null);
        availabilitySlotRepository.save(availabilitySlot);
    }

    private void isDateRangeValidForAdd(AvailabilitySlot availabilitySlot) {
        var availabilitySlots = availabilitySlotRepository.findByAccommodationId(availabilitySlot.getAccommodationId());
        for (AvailabilitySlot as : availabilitySlots) {
            if (areDateRangesOverlapping(availabilitySlot.getStart(), availabilitySlot.getEnd(), as.getStart(), as.getEnd()) && as.getAccommodationId() == availabilitySlot.getAccommodationId()) {
                throw new AvailabilitySlotException("Date range of the slot you want to create overlaps with an existing one!");
            }
        }
    }

    private boolean areDateRangesOverlapping(LocalDate start, LocalDate end, LocalDate start2, LocalDate end2) {
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
            if (areDateRangesOverlapping(availabilitySlot.getStart(), availabilitySlot.getEnd(), as.getStart(), as.getEnd()) && !as.getId().equals(availabilitySlot.getId())) {
                throw new AvailabilitySlotException("Date range of the slot you want to create overlaps with an existing one!");
            }
        }
    }

    public List<AccommodationSearchDto> getByAvailabilityRange(List<Long> appointmentIds, LocalDate startDate, LocalDate endDate) {
        var availabilitySlots = availabilitySlotRepository.findAllByIdAndAvailabilityRange(appointmentIds, startDate, endDate);
        if (availabilitySlots.size() == 0) {
            throw new AvailabilitySlotException("There are no availability slots found");
        }
        ArrayList<AccommodationSearchDto> validAccommodation = getValidAccommodation(startDate, endDate, availabilitySlots);
        return validAccommodation.stream().distinct().toList();
    }

    private ArrayList<AccommodationSearchDto> getValidAccommodation(LocalDate startDate, LocalDate endDate, List<AvailabilitySlot> availabilitySlots) {
        var validAccommodation = new ArrayList<AccommodationSearchDto>(); // TODO: make this an array of accId + accPrice
        for (AvailabilitySlot as : availabilitySlots) {
            if (!isDateRangeContainedInAnotherDateRange(startDate, endDate, as.getStart(), as.getEnd())) {
                continue;
            }
            var valid = true;
            if (as.getReservations() == null) {
                validAccommodation.add(AccommodationSearchDto.builder().id(as.getAccommodationId()).price(as.getPrice()).build());
                continue;
            }
            for (Reservation res : as.getReservations()) {
                if (areDateRangesOverlapping(startDate, endDate, res.getStart(), res.getEnd())) {
                    valid = false;
                }
            }
            if (valid) validAccommodation.add(AccommodationSearchDto.builder().id(as.getAccommodationId()).price(as.getPrice()).build());
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

    private boolean isDateRangeContainedInAnotherDateRange(LocalDate containedStart, LocalDate containedEnd, LocalDate start, LocalDate end) {
        return (start.isBefore(containedStart) || start.isEqual(containedStart)) && (end.isAfter(containedEnd) || end.isEqual(containedEnd));
    }

    public boolean checkForAccommodationDelete(List<Long> accommodationIds, LocalDate boundary) {
        for (Long id : accommodationIds) {
            var availabilitySlots = availabilitySlotRepository.findFutureReservationsForAccommodation(id, boundary);
            if (!availabilitySlots.isEmpty()) {
                publishMessage(new SlotsDeleteFailed(LocalDateTime.now(), EventType.DELETE_SLOT_FAILED, accommodationIds));
                return false;
            }
        }

        for (Long id: accommodationIds) {
            availabilitySlotRepository.deleteByAccommodationId(id);
        }

        publishMessage(new SlotsDeleted(LocalDateTime.now(), EventType.SLOTS_DELETED));
        return true;
    }

    public void publishMessage(BaseEvent event){ // Assuming you have an instance of MyMessage
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend("myQueue", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
