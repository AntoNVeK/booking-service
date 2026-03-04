package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.GuestDao;
import edu.booking.hotel_booking.dto.Guest;
import edu.booking.hotel_booking.dto.responses.GuestResponse;
import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.mappers.GuestMapper;
import edu.booking.hotel_booking.util.GuestNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;
import java.util.UUID;

@Service
public class GuestService {

    private final GuestDao guestDao;

    public GuestService(GuestDao guestDao) {
        this.guestDao = guestDao;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GuestResponse findGuest(UUID id) {
        try {
            GuestEntity entity = guestDao.findById(id);
            return GuestMapper.toResponse(entity);
        } catch (EmptyResultDataAccessException e) {
            throw new GuestNotFoundException("Гость с таким id: " + id + " не найден");
        }
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GuestResponse addGuest(Guest request) {
        GuestEntity entity = new GuestEntity(
                UUID.randomUUID(),
                request.getFirstName(),
                request.getSecondName(),
                request.getBirthday(),
                request.getPhoneNumber()
        );

        guestDao.save(entity);

        return GuestMapper.toResponse(entity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GuestResponse updateGuest(UUID id, Guest request) {
        GuestEntity entity = new GuestEntity(
                id,
                request.getFirstName(),
                request.getSecondName(),
                request.getBirthday(),
                request.getPhoneNumber()
        );
        guestDao.update(entity);
        return GuestMapper.toResponse(entity);
    }

}
