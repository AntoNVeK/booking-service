package edu.booking.hotel_booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchRequest {
    @NotNull(message = "Дата начала брони обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата и время начала поиска", example = "2005-01-07T15:00:00")
    private LocalDateTime dateStart;

    @NotNull(message = "Дата окончания брони обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата и время окончания поиска", example = "2008-07-02T19:00:00")
    private LocalDateTime dateEnd;
}
