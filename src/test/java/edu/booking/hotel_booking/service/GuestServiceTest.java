package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.GuestDao;
import edu.booking.hotel_booking.dto.Guest;
import edu.booking.hotel_booking.dto.responses.GuestResponse;
import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.mappers.GuestMapper;
import edu.booking.hotel_booking.util.GuestNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuestServiceTest {

    private GuestDao guestDao;
    private GuestService guestService;

    @BeforeEach
    void setup() {
        guestDao = Mockito.mock(GuestDao.class);
        guestService = new GuestService(guestDao);
    }

    @Test
    void findGuest_success() {
        UUID id = UUID.randomUUID();
        GuestEntity entity = new GuestEntity(id, "Ivan", "Ivanov", LocalDate.of(1990, 1, 1), "123");
        when(guestDao.findById(id)).thenReturn(entity);
        GuestResponse result = guestService.findGuest(id);
        assertEquals(entity, GuestMapper.toEntity(result));
        verify(guestDao).findById(id);
    }

    @Test
    void findGuest_notFound() {
        UUID id = UUID.randomUUID();
        when(guestDao.findById(id)).thenThrow(new EmptyResultDataAccessException(1));
        assertThrows(GuestNotFoundException.class, () -> guestService.findGuest(id));
        verify(guestDao).findById(id);
    }

    @Test
    void addGuest_success() {
        Guest guest = new Guest("Ivan", "Ivanov", LocalDate.of(1990, 1, 1), "123");
        guestService.addGuest(guest);
        verify(guestDao, times(1)).save(any(GuestEntity.class));
    }

    @Test
    void updateGuest_success() {
        UUID id = UUID.randomUUID();
        Guest guest = new Guest("Ivan", "Ivanov", LocalDate.of(1990, 1, 1), "123");
        guestService.updateGuest(id, guest);
        verify(guestDao).update(argThat(entity ->
                entity.getId().equals(id)
                        && entity.getFirstName().equals("Ivan")
                        && entity.getSecondName().equals("Ivanov")
                        && entity.getBirthday().equals(LocalDate.of(1990, 1, 1))
                        && entity.getPhoneNumber().equals("123")
        ));
    }
}