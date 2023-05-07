package com.example.reservationmicroservice.repository;

import com.example.reservationmicroservice.model.Accommodation;
import com.example.reservationmicroservice.model.AvailabilitySlot;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends MongoRepository<AvailabilitySlot, String> {
    @Query("{'accommodationId' : {'$in' : ?0}, 'start' :{ '$lte' : ?1, '$lte' :  ?2}, 'end' :{ '$gte' : ?1, '$gte' :  ?2}}")
    List<AvailabilitySlot> findAllByIdAndAvailabilityRange(List<Long> appointmentIds, LocalDate startDate, LocalDate endDate);
}
