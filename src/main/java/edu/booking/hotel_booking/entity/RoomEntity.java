package edu.booking.hotel_booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomEntity {
    private UUID id;
    private Integer floor;
    private Integer number;
    private Integer amount;
}
