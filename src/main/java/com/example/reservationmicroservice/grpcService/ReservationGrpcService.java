package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.model.Reservation;
import com.example.reservationmicroservice.model.ReservationStatus;
import com.example.reservationmicroservice.service.ReservationService;
import communication.BooleanResponse;
import communication.ListReservation;
import communication.MessageResponse;
import communication.ReservationServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.example.reservationmicroservice.mapper.ReservationMapper.convertReservationGrpcToReservation;
import static com.example.reservationmicroservice.mapper.ReservationMapper.convertReservationToReservationGrpc;
import static com.example.reservationmicroservice.mapper.ReservationStatusMapper.convertReservationStatusGrpcToReservationStatus;

@GrpcService
@RequiredArgsConstructor
public class ReservationGrpcService extends ReservationServiceGrpc.ReservationServiceImplBase {

    private final ReservationService reservationService;
    private Logger logger = LoggerFactory.getLogger(ReservationGrpcService.class);

    @Override
    public void createRequest(communication.Reservation request,
                              io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        logger.trace("Request to create a reservation for accommodation with id {} was made", request.getId());
        reservationService.create(convertReservationGrpcToReservation(request));
        responseObserver.onNext(MessageResponse.newBuilder().setMessage("Reservation successfully created.").build());
        responseObserver.onCompleted();

    }

    @Override
    public void findById(communication.Id request,
                         io.grpc.stub.StreamObserver<communication.Reservation> responseObserver) {
        logger.trace("Request to find the reservation with id {} was made", request.getId());
        communication.Reservation res = convertReservationToReservationGrpc(reservationService.findById(request.getId()));
        System.out.println(res.getStatus());
        responseObserver.onNext(convertReservationToReservationGrpc(reservationService.findById(request.getId())));
        responseObserver.onCompleted();
    }

    @Override
    public void findAll(communication.EmptyMessage request,
                        io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        logger.trace("Request to find all reservations was made");
        List<Reservation> reservations = reservationService.findAll();
        List<communication.Reservation> shit = new ArrayList<>();
        for (Reservation res : reservations)
            shit.add(convertReservationToReservationGrpc(res));
        ListReservation retVal = ListReservation.newBuilder().addAllReservations(shit).build();
        responseObserver.onNext(retVal);
        responseObserver.onCompleted();

    }

    @Override
    public void findAllByStatus(communication.Status request,
                                io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        logger.trace("Request to find all reservations with status {} was made", request.getStatus());
        List<Reservation> reservations = reservationService.findAllByStatus(convertReservationStatusGrpcToReservationStatus(request.getStatus()));
        List<communication.Reservation> shit = new ArrayList<>();
        for (Reservation res : reservations)
            shit.add(convertReservationToReservationGrpc(res));
        ListReservation retVal = ListReservation.newBuilder().addAllReservations(shit).build();
        responseObserver.onNext(retVal);
        responseObserver.onCompleted();
    }

    @Override
    public void acceptReservationManual(communication.Id request,
                                        io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        logger.trace("Request to manually accept the reservation with id {} was made", request.getId());
        MessageResponse response;
        try {
            reservationService.accept(request.getId());
            response = MessageResponse.newBuilder().setMessage("Reservation successfully accepted.").build();
        } catch (Exception e) {
            response = MessageResponse.newBuilder().setMessage(e.getMessage()).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void rejectRequest(communication.Id request,
                              io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        logger.trace("Request to reject the reservation with id {} was made", request.getId());
        MessageResponse response;
        try {
            reservationService.reject(request.getId());
            response = MessageResponse.newBuilder().setMessage("Reservation successfully rejected.").build();
        } catch (Exception e) {
            response = MessageResponse.newBuilder().setMessage(e.getMessage()).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void acceptReservationAuto(communication.Reservation request,
                                      io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        logger.trace("Request to automatically accept the reservation for accommodation with id {} was made", request.getId());
        reservationService.createAuto(convertReservationGrpcToReservation(request));
        responseObserver.onNext(MessageResponse.newBuilder().setMessage("Reservation successfully created.").build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancel(communication.Id request,
                       io.grpc.stub.StreamObserver<communication.MessageResponse> responseObserver) {
        logger.trace("Request to cancel the reservation with id {} was made", request.getId());
        MessageResponse response;
        try {
            reservationService.cancel(request.getId());
            response = MessageResponse.newBuilder().setMessage("Successfully canceled.").build();
        } catch (Exception e) {
            response = MessageResponse.newBuilder().setMessage(e.getMessage()).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void findAllByUserId(communication.LongId request,
                                io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        logger.trace("Request to find all reservations with user id {} was made", request.getId());
        List<Reservation> reservations = reservationService.findAllByUserId(request.getId());
        List<communication.Reservation> shit = new ArrayList<>();
        for (Reservation res : reservations)
            if(res.getStatus().equals(ReservationStatus.ACCEPTED) || res.getStatus().equals(ReservationStatus.PENDING))
                shit.add(convertReservationToReservationGrpc(res));
        ListReservation retVal = ListReservation.newBuilder().addAllReservations(shit).build();
        responseObserver.onNext(retVal);
        responseObserver.onCompleted();
    }
    @Override
    public void findAllByHostId(communication.LongId request,
                                io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        logger.trace("Request to find all reservations with host id {} was made", request.getId());
        List<Reservation> reservations = reservationService.findAllByHostId(request.getId());
        List<communication.Reservation> reservationList = new ArrayList<>();
        for (Reservation res : reservations)
            reservationList.add(convertReservationToReservationGrpc(res));
        ListReservation retVal = ListReservation.newBuilder().addAllReservations(reservationList).build();
        responseObserver.onNext(retVal);
        responseObserver.onCompleted();
    }
    @Override
    public void findByAccomodationId(communication.LongId request,
                                     io.grpc.stub.StreamObserver<communication.ListReservation> responseObserver) {
        logger.trace("Request to find all reservations with accommodation id {} was made", request.getId());
        List<Reservation> reservations = reservationService.findByAccomodationId(request.getId());
        List<communication.Reservation> shit = new ArrayList<>();
        for (Reservation res : reservations)
            shit.add(convertReservationToReservationGrpc(res));
        ListReservation retVal = ListReservation.newBuilder().addAllReservations(shit).build();
        responseObserver.onNext(retVal);
        responseObserver.onCompleted();
    }


}
