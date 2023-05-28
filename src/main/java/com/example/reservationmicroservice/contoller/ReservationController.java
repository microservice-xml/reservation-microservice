package com.example.reservationmicroservice.contoller;

import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.ReservationStatus;
import com.example.reservationmicroservice.repository.ReservationRepository;
import com.example.reservationmicroservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping
    public ResponseEntity createRequest(@RequestBody Reservation reservation){
        try{
            reservationService.create(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body("Created.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please try again later...");
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

    @GetMapping("/status/{status}")
    public ResponseEntity findAllByStatus(@PathVariable ReservationStatus status) {
        List<Reservation> reservations = reservationService.findAllByStatus(status);
        if(reservations.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("List is empty.");
        else
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findAllByStatus(status));
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity acceptReservationManual(@PathVariable String id){
        reservationService.accept(id);
        return ResponseEntity.status(HttpStatus.OK).body("Reservation accepted.");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity rejectRequest(@PathVariable String id){
        try{
            reservationService.reject(id);
            return ResponseEntity.status(HttpStatus.OK).body("Reservation rejected.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Try again later...");
        }
    }

    @PostMapping("/auto")
    public ResponseEntity acceptReservationAuto(@RequestBody Reservation reservation){
        reservationService.createAuto(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created.");
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

}
