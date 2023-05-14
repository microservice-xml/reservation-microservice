package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.model.Accommodation;
import com.example.reservationmicroservice.service.AccommodationService;
import com.example.reservationmicroservice.service.AvailabilitySlotService;
import communication.AccommodationServiceGrpc;
import communication.MessageResponse;
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
    private final AccommodationService accommodationService;

    @Override
    public void searchByAvailabilityRange(SearchRequest request, StreamObserver<SearchResponse> responseStreamObserver) {
        var startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        var endDate = LocalDate.of(request.getEndYear(), request.getEndMonth(), request.getEndDay());
        var accommodationIds = availabilitySlotService.getByAvailabilityRange(request.getAccommodationIdsList(), startDate, endDate);

        var searchResponse = communication.SearchResponse.newBuilder().addAllAccommodationIds(accommodationIds).build();

        responseStreamObserver.onNext(searchResponse);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void checkForDelete(communication.CheckDeleteRequest request,
                               io.grpc.stub.StreamObserver<communication.BooleanResponse> responseObserver) {
        var startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        boolean check = availabilitySlotService.checkForAccommodationDelete(request.getAccommodationIdsList(), startDate);
        responseObserver.onNext(communication.BooleanResponse.newBuilder().setAvailable(check).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addAccommodationToReservation(communication.AccommodationRes request,
                                              io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        accommodationService.create(Accommodation.builder().accommodationId(request.getAccId()).city(request.getCity()).build());
        responseObserver.onNext(MessageResponse.newBuilder().setMessage("Success").build());
        responseObserver.onCompleted();
    }
}
