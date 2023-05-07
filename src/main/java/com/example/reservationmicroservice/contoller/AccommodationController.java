package com.example.reservationmicroservice.contoller;

import com.example.reservationmicroservice.model.Accommodation;
import com.example.reservationmicroservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodation")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    public ResponseEntity create(@RequestBody Accommodation accommodation){
        try{
            accommodationService.create(accommodation);
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }

    @GetMapping
    public ResponseEntity findAll(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(accommodationService.findAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity findById(@PathVariable String id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(accommodationService.findById(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable String id){
        try{
            accommodationService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody Accommodation accommodation){
        try{
            accommodationService.update(accommodation);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully updated.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }
}
