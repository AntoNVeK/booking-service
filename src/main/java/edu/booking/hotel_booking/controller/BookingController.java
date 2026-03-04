package edu.booking.hotel_booking.controller;


import edu.booking.hotel_booking.dto.BookingChangeRequest;
import edu.booking.hotel_booking.dto.BookingToCreate;
import edu.booking.hotel_booking.dto.RoomSearchRequest;
import edu.booking.hotel_booking.dto.responses.*;
import edu.booking.hotel_booking.service.BookingService;
import edu.booking.hotel_booking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Брони", description = "Управление бронированием отеля")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @Autowired
    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @Operation(summary = "Добавить новую бронь")
    @ApiResponse(responseCode = "200", description = "Бронь успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса или превышена вместимость комнаты")
    @PostMapping
    public ResponseEntity<BookingResponse> addBooking(@Valid @RequestBody BookingToCreate request) {
        return ResponseEntity.ok(bookingService.addBooking(request));
    }

    @Operation(summary = "Получить бронь по ID")
    @ApiResponse(responseCode = "200", description = "Бронь успешно получена")
    @ApiResponse(responseCode = "404", description = "Бронь с указанным ID не найдена")
    @GetMapping("/{id}")
    public ResponseEntity<FindBookingResponse> getBooking(@PathVariable UUID id) {
        FindBookingResponse response = bookingService.findBooking(id);
        response.setMessage("Бронь найдена");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить бронь по ID")
    @ApiResponse(responseCode = "200", description = "Бронь успешно удалена")
    @ApiResponse(responseCode = "404", description = "Бронь с указанным ID не найдена")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteBooking(@PathVariable UUID id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(new ResponseMessage("Бронь удалена"));
    }

    @Operation(summary = "Поиск свободных комнат")
    @ApiResponse(responseCode = "200", description = "Список доступных комнат успешно получен")
    @ApiResponse(responseCode = "400", description = "Некорректные даты поиска")
    @PostMapping("/search")
    public ResponseEntity<List<RoomResponseWithoutMessage>> searchRooms(@RequestBody RoomSearchRequest request) {
        return ResponseEntity.ok(roomService.findSpareRooms(request));
    }

    @Operation(summary = "Обновить данные о бронировании")
    @ApiResponse(responseCode = "200", description = "Бронь успешно обновлена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса или превышена вместимость комнаты")
    @ApiResponse(responseCode = "404", description = "Бронь с указанным ID не найдена")
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable UUID id, @Valid @RequestBody BookingChangeRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

}
