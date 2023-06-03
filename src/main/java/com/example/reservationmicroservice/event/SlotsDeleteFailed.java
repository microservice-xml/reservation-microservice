package com.example.reservationmicroservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SlotsDeleteFailed extends BaseEvent{

    @Field
    List<Long> accommodationIds;

    public SlotsDeleteFailed(LocalDateTime timestamp, EventType type, List<Long> accommodationIds) {
        super(timestamp, type);
        this.accommodationIds = accommodationIds;
    }

    public SlotsDeleteFailed(List<Long> accommodationIds) {
        this.accommodationIds = accommodationIds;
    }
}
