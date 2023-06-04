package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.dto.NotificationDto;
import com.example.reservationmicroservice.exception.AvailabilitySlotException;
import com.example.reservationmicroservice.exception.CancelException;
import com.example.reservationmicroservice.exception.ReservationException;
import com.example.reservationmicroservice.model.AvailabilitySlot;
import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.ReservationStatus;
import com.example.reservationmicroservice.repository.AccommodationRepository;
import com.example.reservationmicroservice.repository.AvailabilitySlotRepository;
import com.example.reservationmicroservice.repository.ReservationRepository;
import communication.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final AccommodationRepository accommodationRepository;

    public void create(Reservation reservation) {
        isDateRangeInSlot(reservation);
        reservation.setStatus(ReservationStatus.PENDING);
        reservationRepository.save(reservation);
        createNotification(reservation.getHostId(),"You have new reservation request from " +reservation.getStart() +" to "+reservation.getEnd() + " at " + accommodationRepository.findByAccommodationId(availabilitySlotRepository.findById(reservation.getSlotId()).get().getAccommodationId()).get().getCity());
    }

    private void createNotification(Long userId, String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<NotificationDto> requestBody = new HttpEntity<>(NotificationDto.builder().userId(userId).message(message).build());
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8088/notification", HttpMethod.POST,requestBody,String.class);
    }
    public void isDateRangeInSlot(Reservation reservation) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(reservation.getSlotId()).get();
        if (!((reservation.getStart().plusDays(1).isAfter(slot.getStart()) && reservation.getStart().isBefore(slot.getEnd())) && (reservation.getEnd().isAfter(slot.getStart()) && reservation.getEnd().minusDays(1).isBefore(slot.getEnd()))))
            throw new ReservationException("Reservation daterange is out of availability slot daterange.");
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
        updateHighlighted(reservation.getHostId());
        createNotification(reservation.getUserId(),"Your reservation request from " +reservation.getStart() +" to "+reservation.getEnd() + " at " + accommodationRepository.findByAccommodationId(availabilitySlotRepository.findById(reservation.getSlotId()).get().getAccommodationId()).get().getCity() +" is ACCEPTED.");
    }

    private void addReservationInAvailabilitySlot(Reservation reservation) {
        //TODO reduce availability slot, add some check
        checkOverlapping(reservation);
        AvailabilitySlot as = availabilitySlotRepository.findById(reservation.getSlotId()).get();
        as.getReservations().add(reservation);
        availabilitySlotRepository.save(as);
    }

    private void checkOverlapping(Reservation reservation) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(reservation.getSlotId()).get();

        if (slot.getReservations().isEmpty())
            return;

        for (var res : slot.getReservations()) {
            if ((reservation.getStart().plusDays(1).isAfter(res.getStart()) && reservation.getStart().isBefore(res.getEnd())) || (reservation.getEnd().isAfter(res.getStart()) && reservation.getEnd().minusDays(1).isBefore(res.getEnd())) || (reservation.getStart().equals(res.getStart())) && reservation.getEnd().equals(res.getEnd()) || (reservation.getStart().isBefore(res.getStart())) && reservation.getEnd().isAfter(res.getEnd()))
                throw new AvailabilitySlotException("Some reservation already exists");
        }
    }

    private void removeReservationFromAvailabilitySlot(Reservation reservation) throws CancelException {
        AvailabilitySlot as = availabilitySlotRepository.findById(reservation.getSlotId()).get();
        as.getReservations().remove(reservation);
        availabilitySlotRepository.save(as);
    }

    private void rejectAllOther(Reservation reservation) {
        List<Reservation> reservations = reservationRepository.allForReject(reservation.getStart(), reservation.getEnd());
        for (var res : reservations) {
            res.setStatus(ReservationStatus.DECLINED);
            reservationRepository.save(res);
            createNotification(res.getUserId(),"Your reservation request from " +reservation.getStart() +" to "+reservation.getEnd() + " at " + accommodationRepository.findByAccommodationId(availabilitySlotRepository.findById(reservation.getSlotId()).get().getAccommodationId()).get().getCity() +" is DECLINED.");
        }
    }

    public void cancel(String id) throws CancelException {
        Reservation res = reservationRepository.findById(id).get();
        if (res.getStatus().equals(ReservationStatus.PENDING))
            reservationRepository.deleteById(id);
        else
            cancelAccepted(res);
    }

    private void cancelAccepted(Reservation reservation) throws CancelException {
        if (reservation.getStatus().equals(ReservationStatus.DECLINED)) {
            reservationRepository.deleteById(reservation.getId());
            return;
        }
        if (reservation.getStart().isAfter(LocalDate.now().plusDays(1))) {
            removeReservationFromAvailabilitySlot(reservation);
            incPenaltiesOfUser(reservation.getUserId());
            reservation.setStatus(ReservationStatus.CANCELED);
            reservationRepository.save(reservation);
            updateHighlighted(reservation.getHostId());
            createNotification(reservation.getHostId(),"Someone canceled the reservation from " +reservation.getStart() +" to "+reservation.getEnd() + " at " + accommodationRepository.findByAccommodationId(availabilitySlotRepository.findById(reservation.getSlotId()).get().getAccommodationId()).get().getCity());
        } else
            throw new CancelException("You can't cancel your reservation now, there's less than a day left.");
    }

    private void incPenaltiesOfUser(Long userId) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9093)
                .usePlaintext()
                .build();
        UserServiceGrpc.UserServiceBlockingStub blockingStub = UserServiceGrpc.newBlockingStub(channel);
        blockingStub.incPenalties(LongId.newBuilder().setId(userId).build());
    }

    public void createAuto(Reservation reservation) {
        isDateRangeInSlot(reservation);
        reservation.setStatus(ReservationStatus.ACCEPTED);
        checkOverlapping(reservation);
        Reservation res = reservationRepository.save(reservation);
        addReservationInAvailabilitySlot(res);
        updateHighlighted(res.getHostId());
    }

    public void reject(String id) {
        Reservation res = findById(id);
        res.setStatus(ReservationStatus.DECLINED);
        reservationRepository.save(res);
    }

    public List<Reservation> findAllByStatus(ReservationStatus status) {
        return reservationRepository.findAllByStatus(status);
    }

    public List<Reservation> findAllByUserId(Long id) {
        return reservationRepository.findAllByUserId(id);
    }

    public List<Reservation> findByAccomodationId(long id) {
        List<Reservation> allReservations = findAll();
        List<Reservation> retVal = new ArrayList<>();
        for (Reservation res : allReservations)
            if (availabilitySlotRepository.findById(res.getSlotId()).get().getAccommodationId() == id && res.getStatus().equals(ReservationStatus.PENDING))
                retVal.add(res);
        return retVal;
    }

    public boolean checkReservations(long id) {
        List<Reservation> reservations = reservationRepository.allByUserId(LocalDate.now(), id);
        return reservations.isEmpty();
    }

    public List<Reservation> findAllByHostId(long hostId){
        return reservationRepository.findAllByHostId(hostId);
    }

    public void updateHighlighted(Long hostId){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9093)
                .usePlaintext()
                .build();
        ReservationServiceGrpc.ReservationServiceBlockingStub blockingStub = ReservationServiceGrpc.newBlockingStub(channel);
        EmptyMessage message = blockingStub.calculateIsHighlighted(LongId.newBuilder().setId(hostId).build());
    }
}
