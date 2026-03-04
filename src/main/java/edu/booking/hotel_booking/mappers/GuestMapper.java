package edu.booking.hotel_booking.mappers;

import edu.booking.hotel_booking.dto.responses.GuestBaseResponse;
import edu.booking.hotel_booking.dto.responses.GuestResponse;
import edu.booking.hotel_booking.dto.responses.GuestResponseWithoutMessage;
import edu.booking.hotel_booking.entity.GuestEntity;

public class GuestMapper {

    public static GuestResponse toResponse(GuestEntity entity) {
        GuestResponse response = new GuestResponse();
        response.setId(entity.getId());
        response.setFirstName(entity.getFirstName());
        response.setSecondName(entity.getSecondName());
        response.setBirthday(entity.getBirthday());
        response.setPhoneNumber(entity.getPhoneNumber());
        return response;
    }

    public static GuestResponseWithoutMessage toResponseWithoutMessage(GuestEntity entity) {
        GuestResponseWithoutMessage response = new GuestResponseWithoutMessage();
        response.setId(entity.getId());
        response.setFirstName(entity.getFirstName());
        response.setSecondName(entity.getSecondName());
        response.setBirthday(entity.getBirthday());
        response.setPhoneNumber(entity.getPhoneNumber());
        return response;
    }
    public static GuestEntity toEntity(GuestBaseResponse guestResponse) {
        return new GuestEntity(guestResponse.getId(), guestResponse.getSecondName(), guestResponse.getFirstName(), guestResponse.getBirthday(), guestResponse.getPhoneNumber());
    }

}