package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.exception.CancelException;
import com.example.reservationmicroservice.model.AvailabilitySlot;
import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.ReservationStatus;
import com.example.reservationmicroservice.repository.AvailabilitySlotRepository;
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
    private final AvailabilitySlotRepository availabilitySlotRepository;

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
        addReservationInAvailabilitySlot(reservation);
        reservationRepository.save(reservation);
        rejectAllOther(reservation);
    }

    private void addReservationInAvailabilitySlot(Reservation reservation){
        //TODO reduce availability slot, add some check
        AvailabilitySlot as = availabilitySlotRepository.findById(reservation.getSlotId()).get();
        as.getReservations().add(reservation);
        availabilitySlotRepository.save(as);
    }

    private void removeReservationFromAvailabilitySlot(Reservation reservation) throws CancelException{
        AvailabilitySlot as = availabilitySlotRepository.findById(reservation.getSlotId()).get();

        int forDelete = -1;
        for(int i = 0 ; i < as.getReservations().size(); i++){
            if(as.getReservations().get(i).equals(reservation.getId())){
                forDelete=i;
                return;
            }
        }

        if(forDelete!=-1) {
            as.getReservations().remove(forDelete);
            availabilitySlotRepository.save(as);
        }else{
            throw new CancelException("Reservation with ID: "+reservation.getId()+" not exists.");
        }
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
        if(reservation.getStart().isAfter(LocalDate.now().plusDays(1))) {
            //TODO increment numberOfCancel in User

            //TODO make availability slot
            removeReservationFromAvailabilitySlot(reservation);
            reservationRepository.deleteById(reservation.getId());
        }
        else
            throw new CancelException("You can't cancel your reservation now, there's less than a day left.");
    }

    public void createAuto(Reservation reservation) {
        reservation.setStatus(ReservationStatus.ACCEPTED);
        addReservationInAvailabilitySlot(reservation);
        reservationRepository.save(reservation);
    }

    public void reject(String id) {
        Reservation res = findById(id);
        res.setStatus(ReservationStatus.DECLINED);
        reservationRepository.save(res);
    }
}
