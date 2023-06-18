package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
@RequiredArgsConstructor
public class UserCommunicationGrpcService extends communication.UserCommunicationServiceGrpc.UserCommunicationServiceImplBase {

    private final ReservationService reservationService;
    private Logger logger = LoggerFactory.getLogger(UserCommunicationGrpcService.class);

    @Override
    public void getReservation(communication.UserIdRequest request,
                               io.grpc.stub.StreamObserver<communication.BooleanResponse> responseObserver) {
            logger.trace("Request to check if user with id {} has any reservations was made", request.getId());
            boolean check = reservationService.checkReservations(request.getId());
            communication.BooleanResponse response = communication.BooleanResponse.newBuilder().setAvailable(check).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }
}
