package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.exception.CancelException;
import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.ReservationStatus;
import com.example.reservationmicroservice.repository.ReservationRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public void create(Reservation reservation) {
        reservation.setStatus(ReservationStatus.PENDING);
        reservationRepository.save(reservation);
    }

    public Reservation findById(String id) {
        return reservationRepository.findById(id).get();
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public void accept(String id) {
        Reservation reservation = findById(id);
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation);
        rejectAllOther(reservation);
    }

    private void rejectAllOther(Reservation reservation) {
        List<Reservation> reservations = reservationRepository.allForReject(reservation.getStart(), reservation.getEnd());
        for(var res : reservations){
            res.setStatus(ReservationStatus.DECLINED);
            reservationRepository.save(res);
        }
    }

    public void cancel(String id) throws CancelException {
        Reservation res = reservationRepository.findById(id).get();
        if(res.getStatus().equals(ReservationStatus.PENDING))
            reservationRepository.deleteById(id);
        else
            cancelAccepted(res);
    }

    private void cancelAccepted(Reservation reservation) throws CancelException {
        if(reservation.getStatus().equals(ReservationStatus.DECLINED))
            return;
        if(reservation.getStart().isAfter(LocalDate.now().plusDays(1)))
            //TODO increment numberOfCancel in User
            //TODO make availability slot
            reservationRepository.deleteById(reservation.getId());
        else
            throw new CancelException();
    }
}
