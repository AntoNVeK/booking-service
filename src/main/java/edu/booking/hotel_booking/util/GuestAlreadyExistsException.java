package edu.booking.hotel_booking.util;

public class GuestAlreadyExistsException extends RuntimeException {
    public GuestAlreadyExistsException(String message) {
        super(message);
    }
}
