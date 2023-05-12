package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.service.ReservationService;
import communication.ReservationServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ReservationGrpcService extends ReservationServiceGrpc.ReservationServiceImplBase {

    private final ReservationService reservationService;
    public void createRequest(communication.Reservation request,
                              io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        //TODO
    }
    public void findById(communication.Id request,
                         io.grpc.stub.StreamObserver<communication.Reservation> responseObserver) {
        //TODO
    }
    public void findAll(communication.EmptyMessage request,
                        io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        //TODO
    }
    public void findAllByStatus(communication.Status request,
                                io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        //TODO
    }
    public void acceptReservationManual(communication.Id request,
                                        io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        //TODO
    }
    public void rejectRequest(communication.Id request,
                              io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        //TODO
    }
    public void acceptReservationAuto(communication.Reservation request,
                                      io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        //TODO
    }
    public void cancel(communication.Id request,
                       io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        //TODO
    }
}
