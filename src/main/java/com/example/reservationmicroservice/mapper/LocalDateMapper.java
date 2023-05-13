package com.example.reservationmicroservice.mapper;

import com.google.protobuf.Timestamp;
import com.mongodb.internal.connection.LoadBalancedClusterableServerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class LocalDateMapper {
    public static LocalDate convertGoogleTimestampToLocalDate(Timestamp timestamp) {
        return Instant
                .ofEpochSecond(timestamp.getSeconds() , timestamp.getNanos())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
    }
    public static Timestamp convertLocalDateToGoogleTimestamp(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
