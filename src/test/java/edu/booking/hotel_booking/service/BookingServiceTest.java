package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.BookingDao;
import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.dto.BookingToCreate;
import edu.booking.hotel_booking.entity.BookingEntity;
import edu.booking.hotel_booking.entity.CreateBookingEntity;
import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.entity.RoomEntity;
import edu.booking.hotel_booking.util.BookingNotFoundException;
import edu.booking.hotel_booking.util.BusyRoomException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {


    private BookingDao bookingDao;
    private RoomDao roomDao;
    private BookingService bookingService;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setup() {
        bookingDao = mock(BookingDao.class);
        roomDao = mock(RoomDao.class);

        meterRegistry = new SimpleMeterRegistry();

        bookingService = new BookingService(bookingDao, roomDao, meterRegistry);
    }

    @Test
    void addBookingSuccess() {
        UUID roomId = UUID.randomUUID();
        BookingToCreate request = new BookingToCreate(LocalDateTime.now(), LocalDateTime.now().plusDays(1), roomId, List.of(UUID.randomUUID()));
        when(bookingDao.isAvailable(any(), any(), any())).thenReturn(true);
        bookingService.addBooking(request);
        verify(roomDao).checkAndLockRoom(roomId);
        verify(bookingDao).isAvailable(roomId, request.getDateStart(), request.getDateEnd());
        verify(bookingDao).save(any(CreateBookingEntity.class));
    }

    @Test
    void addBookingRoomIsBusy() {
        UUID roomId = UUID.randomUUID();
        BookingToCreate request = new BookingToCreate(LocalDateTime.now(), LocalDateTime.now().plusDays(1), roomId, List.of(UUID.randomUUID()));

        when(bookingDao.isAvailable(any(), any(), any())).thenReturn(false);

        assertThrows(BusyRoomException.class, () -> bookingService.addBooking(request));
        verify(roomDao).checkAndLockRoom(roomId);
        verify(bookingDao).isAvailable(roomId, request.getDateStart(), request.getDateEnd());
        verify(bookingDao, never()).save(any());
    }

    @Test
    void addBookingRoomNotFound() {
        UUID roomId = UUID.randomUUID();
        BookingToCreate request = new BookingToCreate(LocalDateTime.now(), LocalDateTime.now().plusDays(1), roomId, List.of(UUID.randomUUID()));

        doThrow(new RuntimeException("Комната не найдена")).when(roomDao).checkAndLockRoom(roomId);
        assertThrows(RuntimeException.class, () -> bookingService.addBooking(request));
        verify(bookingDao, never()).save(any());
    }

    @Test
    void findBookingSuccess() {
        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setFloor(3);
        room.setNumber(305);
        room.setAmount(2);

        GuestEntity guest1 = new GuestEntity(UUID.randomUUID(), "Степанов", "Степан", null, null);
        GuestEntity guest2 = new GuestEntity(UUID.randomUUID(), "Сидоров", "Петров", null, null);
        List<GuestEntity> guests = List.of(guest1, guest2);

        UUID bookingId = UUID.randomUUID();
        BookingEntity booking = new BookingEntity();
        booking.setId(bookingId);
        booking.setDateStart(LocalDateTime.of(2024, 6, 10, 14, 0));
        booking.setDateEnd(LocalDateTime.of(2024, 6, 15, 12, 0));
        booking.setRoom(room);
        booking.setGuestList(guests);

        when(bookingDao.findById(bookingId)).thenReturn(booking);

        var response = bookingService.findBooking(bookingId);

        assertNotNull(response);
        assertEquals(bookingId, response.getId());
        assertEquals(3, response.getRoom().getFloor());
        assertEquals(2, response.getRoom().getAmount());
        assertEquals(2, response.getGuests().size());

        verify(bookingDao, times(1)).findById(bookingId);
    }


    @Test
    void findBookingNotFound() {
        UUID id = UUID.randomUUID();
        when(bookingDao.findById(id)).thenThrow(new EmptyResultDataAccessException(1));
        assertThrows(BookingNotFoundException.class, () -> bookingService.findBooking(id));
    }

    @Test
    void deleteBookingSuccess() {
        UUID id = UUID.randomUUID();
        bookingService.deleteBooking(id);
        verify(bookingDao).delete(id);
    }

    @Test
    void deleteBookingNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new BookingNotFoundException("Бронь не найдена")).when(bookingDao).delete(id);
        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(id));
    }
}