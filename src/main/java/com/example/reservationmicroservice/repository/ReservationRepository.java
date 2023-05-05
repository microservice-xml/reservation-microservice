package com.example.reservationmicroservice.repository;

import com.example.reservationmicroservice.model.Reservation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, ObjectId> {
}
