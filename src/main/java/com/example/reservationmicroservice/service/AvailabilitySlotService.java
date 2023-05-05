package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.repository.AvailabilitySlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;
}
