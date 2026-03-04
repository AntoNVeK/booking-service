package edu.booking.hotel_booking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomBaseResponse {
    private UUID id;
    @Schema(description = "Этаж комнаты", example = "3")
    private Integer floor;
    @Schema(description = "Номер комнаты", example = "305")
    private Integer number;
    @Schema(description = "Количество мест в комнате", example = "2")
    private Integer amount;
}
