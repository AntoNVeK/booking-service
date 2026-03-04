package edu.booking.hotel_booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestEntity {
    private UUID id;
    private String secondName;
    private String firstName;
    private LocalDate birthday;
    private String phoneNumber;
}
