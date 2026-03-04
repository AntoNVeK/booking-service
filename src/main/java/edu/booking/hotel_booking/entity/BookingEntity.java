package edu.booking.hotel_booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {
    private UUID id;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private RoomEntity room;
    private List<GuestEntity> guestList;
}
