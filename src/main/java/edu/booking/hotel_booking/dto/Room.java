package edu.booking.hotel_booking.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @NotNull(message = "Этаж обязателен")
    @Schema(description = "Этаж комнаты", example = "3")
    private Integer floor;

    @NotNull(message = "Номер обязателен")
    @Schema(description = "Номер комнаты", example = "305")
    private Integer number;

    @NotNull(message = "Количество мест обязательно")
    @Schema(description = "Количество мест в комнате", example = "2")
    private Integer amount;
}
