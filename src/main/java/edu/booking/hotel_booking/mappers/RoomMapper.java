package edu.booking.hotel_booking.mappers;

import edu.booking.hotel_booking.dto.responses.RoomBaseResponse;
import edu.booking.hotel_booking.dto.responses.RoomResponse;
import edu.booking.hotel_booking.dto.responses.RoomResponseWithoutMessage;
import edu.booking.hotel_booking.entity.RoomEntity;

public class RoomMapper {

    public static RoomResponse toResponse(RoomEntity entity) {
        RoomResponse response = new RoomResponse();
        response.setId(entity.getId());
        response.setFloor(entity.getFloor());
        response.setNumber(entity.getNumber());
        response.setAmount(entity.getAmount());
        return response;
    }

    public static RoomResponseWithoutMessage toResponseWithoutMessage(RoomEntity entity) {
        RoomResponseWithoutMessage response = new RoomResponseWithoutMessage();
        response.setId(entity.getId());
        response.setFloor(entity.getFloor());
        response.setNumber(entity.getNumber());
        response.setAmount(entity.getAmount());
        return response;
    }

    public static RoomEntity toEntity(RoomBaseResponse response) {
        return new RoomEntity(response.getId(), response.getFloor(), response.getNumber(), response.getAmount());
    }


}