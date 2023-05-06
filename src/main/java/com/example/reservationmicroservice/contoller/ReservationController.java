package com.example.reservationmicroservice.contoller;

import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.repository.ReservationRepository;
import com.example.reservationmicroservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping
    public ResponseEntity create(@RequestBody Reservation reservation){
        try{
            reservationService.create(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body("Created.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please try again later");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.findById(id));
    }

    @GetMapping()
    public ResponseEntity findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity acceptReservation(@PathVariable String id){
        try{
            reservationService.accept(id);
            return ResponseEntity.status(HttpStatus.OK).body("Reservation accepted.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity cancel(@PathVariable String id){
        try{
            reservationService.cancel(id);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully canceled.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @GetMapping(value = "/test/{id}")
//    public ResponseEntity test(@PathVariable String id){
//        Reservation res = reservationService.findById(id);
//        return ResponseEntity.status(HttpStatus.CREATED).body(reservationRepository.allForReject(res.getStart(),res.getEnd()));
//    }

}
