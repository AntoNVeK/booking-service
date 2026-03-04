package edu.booking.hotel_booking.dto.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindBookingResponse {
    private UUID id;
    @Schema(description = "Дата и время начала брони", example = "2007-03-01T14:00:00")
    private LocalDateTime dateStart;
    @Schema(description = "Дата и время окончания брони", example = "2009-03-05T12:00:00")
    private LocalDateTime dateEnd;
    @Schema(description = "Комнаты")
    private RoomResponseWithoutMessage room;
    @Schema(description = "Список гостей")
    private List<GuestResponseWithoutMessage> guests;
    @Schema(description = "Сообщение ответа")
    private String message;
}
