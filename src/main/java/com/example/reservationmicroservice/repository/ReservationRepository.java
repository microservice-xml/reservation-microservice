package com.example.reservationmicroservice.repository;

import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.ReservationStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {
    @Query("{$or : [ {'start' : { $gt :?0 , $lt: ?1 }} , {'end' : { $gt :?0 , $lt: ?1 }} ] , 'status': 'PENDING', }")
    List<Reservation> allForReject(LocalDate start, LocalDate end);

    List<Reservation> findAllByStatus(ReservationStatus status);

    List<Reservation> findAllByUserId(Long id);

    List<Reservation> findAllByHostId(Long id);

    @Query("{$and: [ {'start' : { $gt : ?0}}, {'status' : 'ACCEPTED'}, {'userId' : ?1 }]}")
    List<Reservation> allByUserId(LocalDate start, Long userId);
}
