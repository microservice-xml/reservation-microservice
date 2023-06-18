package com.example.reservationmicroservice.grpcService;

import com.example.reservationmicroservice.service.AvailabilitySlotService;
import communication.AvailabilitySlot;
import communication.AvailabilitySlotServiceGrpc;
import communication.EmptyMessage;
import communication.ListAvailabilitySlotFull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.example.reservationmicroservice.mapper.AvailabilitySlotMapper.convertAvailabilitySlotGrpcToAvailabilitySlot;
import static com.example.reservationmicroservice.mapper.AvailabilitySlotMapper.convertAvailabilitySlotToAvailabilitySlotGrpc;

@GrpcService
@RequiredArgsConstructor
public class AvailabilitySlotGrpcService extends AvailabilitySlotServiceGrpc.AvailabilitySlotServiceImplBase {
    private final AvailabilitySlotService availabilitySlotService;
    private Logger logger = LoggerFactory.getLogger(AvailabilitySlotGrpcService.class);

    @Override
    public void findAllAvailabilitySlotsByAccommodationId(communication.LongId request,
                                                          io.grpc.stub.StreamObserver<communication.ListAvailabilitySlotFull> responseObserver) {
        logger.trace("Request to find all availability slots for accommodation with id {} was made", request.getId());
        List<com.example.reservationmicroservice.model.AvailabilitySlot> availabilitySlots = availabilitySlotService.getAllByAccommodationId(request.getId());
        List<communication.AvailabilitySlotFull> availabilitySlotFulls = new ArrayList<>();
        for (com.example.reservationmicroservice.model.AvailabilitySlot as: availabilitySlots) {
            availabilitySlotFulls.add(convertAvailabilitySlotToAvailabilitySlotGrpc(as));
        }
        ListAvailabilitySlotFull retVal = ListAvailabilitySlotFull.newBuilder().addAllAvailabilitySlots(availabilitySlotFulls).build();
        responseObserver.onNext(retVal);
        responseObserver.onCompleted();
    }

    @Override
    public void add(communication.AvailabilitySlotFull request,
                                                          io.grpc.stub.StreamObserver<communication.EmptyMessage> responseObserver) {
        logger.trace("Request to add an availability slot for accommodation with id {} was made", request.getId());
        availabilitySlotService.add(convertAvailabilitySlotGrpcToAvailabilitySlot(request));
        responseObserver.onNext(EmptyMessage.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void edit(communication.AvailabilitySlotFull request,
                    io.grpc.stub.StreamObserver<communication.EmptyMessage> responseObserver) {
        logger.trace("Request to edit an availability slot for accommodation with id {} was made", request.getId());
        availabilitySlotService.edit(convertAvailabilitySlotGrpcToAvailabilitySlot(request));
        responseObserver.onNext(EmptyMessage.newBuilder().build());
        responseObserver.onCompleted();
    }
}
