package edu.booking.hotel_booking.util;

public class BusyRoomException extends RuntimeException {
    public BusyRoomException(String message) {
        super(message);
    }
}
