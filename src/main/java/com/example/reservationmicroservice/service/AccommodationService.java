package com.example.reservationmicroservice.service;

import com.example.reservationmicroservice.model.Accommodation;
import com.example.reservationmicroservice.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;

    public void create(Accommodation accommodation) {
        accommodationRepository.save(accommodation);
    }

    public List<Accommodation> findAll() {
        return accommodationRepository.findAll();
    }

    public Accommodation findById(String id) {
        return accommodationRepository.findById(id).get();
    }

    public void deleteById(String id) {
        accommodationRepository.deleteById(id);
    }

    public void update(Accommodation accommodation) {
        Accommodation acc = findById(accommodation.getId());
        acc.setCity(accommodation.getCity());
        acc.setAccommodationId(accommodation.getAccommodationId());
        accommodationRepository.save(acc);
    }
}
