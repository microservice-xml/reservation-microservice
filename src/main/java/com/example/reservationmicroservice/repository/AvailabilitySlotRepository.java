package com.example.reservationmicroservice.repository;

import com.example.reservationmicroservice.model.AvailabilitySlot;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilitySlotRepository extends MongoRepository<AvailabilitySlot, String> {
}
