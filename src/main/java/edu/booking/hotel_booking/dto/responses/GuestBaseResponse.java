package edu.booking.hotel_booking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestBaseResponse {
    private UUID id;
    @Schema(description = "Фамилия гостя", example = "Иванов")
    private String secondName;
    @Schema(description = "Имя гостя", example = "Иван")
    private String firstName;
    @Schema(description = "Дата рождения гостя", example = "2005-05-10")
    private LocalDate birthday;
    @Schema(description = "Телефон гостя", example = "+7-321-133-12-44")
    private String phoneNumber;

}
