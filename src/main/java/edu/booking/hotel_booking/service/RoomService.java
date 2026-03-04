package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.dto.Room;
import edu.booking.hotel_booking.dto.RoomSearchRequest;
import edu.booking.hotel_booking.dto.responses.RoomResponse;
import edu.booking.hotel_booking.dto.responses.RoomResponseWithoutMessage;
import edu.booking.hotel_booking.entity.RoomEntity;
import edu.booking.hotel_booking.mappers.RoomMapper;
import edu.booking.hotel_booking.util.RoomNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {


    private final RoomDao roomDao;

    public RoomService(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RoomResponse addRoom(Room request) {
        RoomEntity entity = new RoomEntity(
                UUID.randomUUID(),
                request.getFloor(),
                request.getNumber(),
                request.getAmount()
        );

        roomDao.save(entity);
        return RoomMapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RoomResponse updateRoom(UUID id, Room request) {
        RoomEntity entity = new RoomEntity(
                id,
                request.getFloor(),
                request.getNumber(),
                request.getAmount()
        );

        roomDao.update(entity);

        return RoomMapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RoomResponse findRoom(UUID id) {
        try {
            return RoomMapper.toResponse(roomDao.findById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new RoomNotFoundException("Комната с таким id: " + id + " не найдена");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteRoom(UUID id) {
        roomDao.delete(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<RoomResponseWithoutMessage> findSpareRooms(RoomSearchRequest request) {
        if (request.getDateStart().isAfter(request.getDateEnd()) || request.getDateStart().isEqual(request.getDateEnd())) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты окончания");
        }
        List<RoomEntity> rooms = roomDao.findSpareRooms(request);


        if (rooms.isEmpty()) {
            throw new RoomNotFoundException("Нет свободных номеров с " + request.getDateStart().toString() + " по " + request.getDateEnd().toString());
        }

        return rooms.stream()
                .map(RoomMapper::toResponseWithoutMessage)
                .toList();
    }
}
