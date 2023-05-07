package com.example.reservationmicroservice.contoller;

import com.example.reservationmicroservice.model.AvailabilitySlot;
import com.example.reservationmicroservice.service.AvailabilitySlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AvailabilitySlotController {
    private final AvailabilitySlotService availabilitySlotService;

    @PostMapping
    public ResponseEntity add(@RequestBody AvailabilitySlot availabilitySlot) {
        availabilitySlotService.add(availabilitySlot);
        return ResponseEntity.status(HttpStatus.OK).body("Availability slot created successfully.");
    }

    @PutMapping
    public ResponseEntity edit(@RequestBody AvailabilitySlot availabilitySlot) {
        availabilitySlotService.edit(availabilitySlot);
        return ResponseEntity.status(HttpStatus.OK).body("Availability slot updated successfully.");
    }
}
