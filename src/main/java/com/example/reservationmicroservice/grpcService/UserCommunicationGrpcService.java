package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.service.ReservationService;
import communication.UserCommunicationServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class UserCommunicationGrpcService extends UserCommunicationServiceGrpc.UserCommunicationServiceImplBase {

    private final ReservationService reservationService;

    @Override
    public void getReservation(communication.UserIdRequest request,
                               io.grpc.stub.StreamObserver<communication.BooleanResponse> responseObserver) {
            boolean check = reservationService.checkReservations(request.getId());
            communication.BooleanResponse response = communication.BooleanResponse.newBuilder().setAvailable(check).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }
}
