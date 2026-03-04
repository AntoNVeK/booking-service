package edu.booking.hotel_booking.controller;



import edu.booking.hotel_booking.dto.Room;
import edu.booking.hotel_booking.dto.responses.ResponseMessage;
import edu.booking.hotel_booking.dto.responses.RoomResponse;
import edu.booking.hotel_booking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Комнаты", description = "Управление комнатами отеля")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Добавить новую комнату")
    @ApiResponse(responseCode = "200", description = "Комната успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Неверные данные комнаты")
    @PostMapping
    public ResponseEntity<RoomResponse> addRoom(@Valid @RequestBody Room request) {
        RoomResponse response = roomService.addRoom(request);
        response.setMessage("Успешное добавление");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить данные комнаты")
    @ApiResponse(responseCode = "200", description = "Комната успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @ApiResponse(responseCode = "400", description = "Неверные данные комнаты")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable UUID id, @Valid @RequestBody Room request) {
        RoomResponse response = roomService.updateRoom(id, request);
        response.setMessage("Успешное обновление");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить данные комнаты")
    @ApiResponse(responseCode = "200", description = "Комната успешно получена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable UUID id) {
        RoomResponse response = roomService.findRoom(id);
        response.setMessage("Успешный поиск");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить комнату")
    @ApiResponse(responseCode = "200", description = "Комната успешно удалена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteRoom(@PathVariable UUID id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(new ResponseMessage("Комната удалена"));

    }
}
