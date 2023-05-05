package com.example.reservationmicroservice.contoller;

import com.example.reservationmicroservice.service.AvailabilitySlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AvailvabilitySlotController {
    private final AvailabilitySlotService availabilitySlotService;
}
