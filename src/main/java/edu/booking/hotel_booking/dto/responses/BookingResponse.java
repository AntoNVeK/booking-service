package edu.booking.hotel_booking.dto.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.UUID;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private UUID id;

    @Schema(description = "Сообщение ответа")
    private String message;
}
