package edu.booking.hotel_booking.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingChangeRequest {
    @NotNull(message = "Дата начала брони обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата и время начала брони", example = "2007-03-01T14:00:00")
    private LocalDateTime dateStart;

    @NotNull(message = "Дата окончания брони обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата и время окончания брони", example = "2009-03-05T12:00:00")
    private LocalDateTime dateEnd;

    @NotNull(message = "Необходимо указать номер")
    @Schema(description = "ID номера комнаты")
    private UUID roomId;

    @Schema(description = "Список ID гостей")
    private List<UUID> guestIds;

}
