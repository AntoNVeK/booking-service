package edu.booking.hotel_booking.mappers;


import edu.booking.hotel_booking.dto.responses.FindBookingResponse;
import edu.booking.hotel_booking.entity.BookingEntity;

public class BookingMapper {

    public static FindBookingResponse toFindResponse(BookingEntity entity) {
        FindBookingResponse response = new FindBookingResponse();
        response.setId(entity.getId());
        response.setDateStart(entity.getDateStart());
        response.setDateEnd(entity.getDateEnd());
        response.setRoom(RoomMapper.toResponseWithoutMessage(entity.getRoom()));
        response.setGuests(entity.getGuestList().stream()
                .map(GuestMapper::toResponseWithoutMessage)
                .toList());

        return response;
    }


    public static BookingEntity toEntity(FindBookingResponse response) {
        return new BookingEntity(response.getId(), response.getDateStart(), response.getDateEnd(),
                RoomMapper.toEntity(response.getRoom()),
                response.getGuests().stream()
                .map(GuestMapper::toEntity)
                .toList());
    }
}
