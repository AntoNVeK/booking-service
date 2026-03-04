package edu.booking.hotel_booking.controller;

import edu.booking.hotel_booking.dto.Guest;
import edu.booking.hotel_booking.dto.responses.GuestResponse;
import edu.booking.hotel_booking.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/guests")
@Tag(name = "Гости", description = "Управление гостями отеля")
public class GuestController {

    private final GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @Operation(summary = "Добавить гостя")
    @ApiResponse(responseCode = "200", description = "Гость успешно добавлен")
    @ApiResponse(responseCode = "400", description = "Не корректные данные")
    @PostMapping
    public ResponseEntity<GuestResponse> addGuest(@Valid @RequestBody Guest request) {
        GuestResponse response = guestService.addGuest(request);
        response.setMessage("Успешное добавление");
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Обновить данные гостя по ID")
    @ApiResponse(responseCode = "200", description = "Гость успешно удалена")
    @ApiResponse(responseCode = "400", description = "Не корректные данные")
    @ApiResponse(responseCode = "404", description = "Гость не найден")
    @PutMapping("/{id}")
    public ResponseEntity<GuestResponse> updateGuest(@PathVariable UUID id, @Valid @RequestBody Guest request) {
        GuestResponse response = guestService.updateGuest(id, request);
        response.setMessage("Успешное обновление");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить данные гостя по ID")
    @ApiResponse(responseCode = "200", description = "Гость успешно получен")
    @ApiResponse(responseCode = "404", description = "Гость не найден")
    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getGuest(@PathVariable UUID id) {
        GuestResponse response = guestService.findGuest(id);
        response.setMessage("Успешный поиск");
        return ResponseEntity.ok(response);
    }

}