package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.BookingDao;
import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.dto.BookingChangeRequest;
import edu.booking.hotel_booking.dto.BookingToCreate;
import edu.booking.hotel_booking.dto.responses.BookingResponse;
import edu.booking.hotel_booking.dto.responses.FindBookingResponse;
import edu.booking.hotel_booking.dto.responses.RoomResponse;
import edu.booking.hotel_booking.entity.CreateBookingEntity;
import edu.booking.hotel_booking.mappers.BookingMapper;
import edu.booking.hotel_booking.util.BookingNotFoundException;
import edu.booking.hotel_booking.util.BusyRoomException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;



import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BookingService {

    private final BookingDao bookingDao;
    private final RoomDao roomDao;
    private Counter bookingCreatedCounter;
    private Timer bookingCreationTimer;
    private AtomicInteger activeBookCreationRequests;

    @Autowired
    public BookingService(BookingDao bookingDao, RoomDao roomDao, MeterRegistry registry) {
        this.bookingDao = bookingDao;
        this.roomDao = roomDao;


        bookingCreatedCounter = Counter.builder("bookings.created")
                .description("Количество созданных броней")
                .register(registry);

        bookingCreationTimer = Timer.builder("bookings.creation.time")
                .description("Время на создание брони")
                .register(registry);

        activeBookCreationRequests = new AtomicInteger(0);
        Gauge.builder("bookings.creation.active.requests", activeBookCreationRequests, AtomicInteger::get)
                .description("Активных запросов на создание брони")
                .register(registry);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse addBooking(BookingToCreate request) {
        activeBookCreationRequests.incrementAndGet();
        long start = System.nanoTime();

        try {

            roomDao.checkAndLockRoom(request.getRoomId());

            if (!bookingDao.isAvailable(request.getRoomId(), request.getDateStart(), request.getDateEnd())) {
                throw new BusyRoomException("Комната занята");
            }

            CreateBookingEntity entity = new CreateBookingEntity(
                    UUID.randomUUID(),
                    request.getDateStart(),
                    request.getDateEnd(),
                    request.getRoomId(),
                    request.getGuestIds()
            );

            bookingCreatedCounter.increment();
            bookingDao.save(entity);

            return new BookingResponse(entity.getId(), "Бронь добавлена");
        } finally {

            long duration = System.nanoTime() - start;
            bookingCreationTimer.record(duration, TimeUnit.NANOSECONDS);
            activeBookCreationRequests.decrementAndGet();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FindBookingResponse findBooking(UUID id) {
        try {
            return BookingMapper.toFindResponse(bookingDao.findById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new BookingNotFoundException("Нет брони с id: " + id);
        }
    }

    public void deleteBooking(UUID id) {
        bookingDao.delete(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse updateBooking(UUID id, BookingChangeRequest request) {
        roomDao.checkAndLockRoom(request.getRoomId());

        if (!bookingDao.checkExistById(id)) {
            throw new BookingNotFoundException("Нет брони с id: " + id);
        }

        if (!bookingDao.isAvailableExcludingCurrent(
                request.getRoomId(),
                request.getDateStart(),
                request.getDateEnd(),
                id)) {
            throw new BusyRoomException("Комната занята в указанный период");
        }

        bookingDao.update(id, request);
        return new BookingResponse(id, "Бронь изменена");
    }

}

