package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.service.AvailabilitySlotService;
import communication.AccommodationServiceGrpc;
import communication.SearchRequest;
import communication.SearchResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;

@GrpcService
@RequiredArgsConstructor
public class AccommodationGrpcService extends AccommodationServiceGrpc.AccommodationServiceImplBase {

    private final AvailabilitySlotService availabilitySlotService;

    @Override
    public void searchByAvailabilityRange(SearchRequest request, StreamObserver<SearchResponse> responseStreamObserver) {
        var startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        var endDate = LocalDate.of(request.getEndYear(), request.getEndMonth(), request.getEndDay());
        var accommodationIds = availabilitySlotService.getByAvailabilityRange(request.getAccommodationIdsList(), startDate, endDate);

        var searchResponse = communication.SearchResponse.newBuilder().addAllAccommodationIds(accommodationIds).build();

        responseStreamObserver.onNext(searchResponse);
        responseStreamObserver.onCompleted();
    }
}
