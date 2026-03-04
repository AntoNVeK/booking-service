package edu.booking.hotel_booking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse extends RoomBaseResponse {
    @Schema(description = "Сообщение ответа")
    private String message;
}






