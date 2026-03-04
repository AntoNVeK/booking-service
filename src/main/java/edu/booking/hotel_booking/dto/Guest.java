package edu.booking.hotel_booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guest {

    @NotBlank(message = "Фамилия не может быть пустой")
    @Schema(description = "Фамилия гостя", example = "Иванов")
    private String secondName;

    @NotBlank(message = "Имя не может быть пустым")
    @Schema(description = "Имя гостя", example = "Иван")
    private String firstName;

    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата рождения гостя", example = "2005-05-10")
    private LocalDate birthday;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(
            regexp = "^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Номер телефона должен быть в формате: +7-xxx-xxx-xx-xx"
    )
    @Schema(description = "Телефон гостя", example = "+7-321-133-12-44")
    private String phoneNumber;
}