package edu.booking.hotel_booking.dao;


import edu.booking.hotel_booking.dto.Room;
import edu.booking.hotel_booking.dto.RoomSearchRequest;
import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.entity.RoomEntity;
import edu.booking.hotel_booking.util.BookingNotFoundException;
import edu.booking.hotel_booking.util.BusyRoomException;
import edu.booking.hotel_booking.util.RoomAlreadyExistsException;
import edu.booking.hotel_booking.util.RoomNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class RoomDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RoomDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RoomEntity selectById(UUID id, boolean forUpdate) {
        String sql = "select id, floor, number_room as number, amount from room where id = :id"
                + (forUpdate ? " for update" : "");

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(RoomEntity.class));
        } catch (EmptyResultDataAccessException e) {
            throw new RoomNotFoundException("Комната с id " + id + " не найдена");
        }
    }

    public RoomEntity findById(UUID id) {
        return selectById(id, false);
    }

    public void checkAndLockRoom(UUID id) {
        selectById(id, true);
    }

    public void save(RoomEntity room) {

        String sql = """
                insert into room (id, floor, number_room, amount)
                        values (:id, :floor, :number, :amount);
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", room.getId())
                .addValue("floor", room.getFloor())
                .addValue("number", room.getNumber())
                .addValue("amount", room.getAmount());

        try {
            jdbcTemplate.update(sql, params);
        } catch (DuplicateKeyException e) {
            throw new RoomAlreadyExistsException("Комната с таким номером и этажом уже существует");
        }
    }


    public void update(RoomEntity room) {
        String sql = """
                update room
                set floor = :floor,
                    number_room = :number,
                    amount = :amount
                where id = :id
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", room.getId())
                .addValue("floor", room.getFloor())
                .addValue("number", room.getNumber())
                .addValue("amount", room.getAmount());

        try {
            int updatedRows = jdbcTemplate.update(sql, params);
            if (updatedRows == 0) {
                throw new RoomNotFoundException("Комната с id " + room.getId() + " не найден");
            }
        } catch (DuplicateKeyException e) {
            throw new RoomAlreadyExistsException("Комната с таким номером и этажом уже существует");
        } catch (Exception e) {
            throw new IllegalArgumentException("Нельзя уменьшить количество мест: есть бронирование с большим числом гостей", e);
        }

    }

    public void delete(UUID id) {
        String sql = """
                delete from room
                where id = :id
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        int deleteRow = jdbcTemplate.update(sql, params);
        if (deleteRow == 0) {
            throw new RoomNotFoundException("Комната с id " + id + " не найден");
        }
    }


    public List<RoomEntity> findSpareRooms(RoomSearchRequest request) {
        String sql = """
            select id, floor, number_room as number, amount from room
            where id not in (
                select distinct room_id
                from booking
                where date_start < :dateEnd
                and date_end > :dateStart
            )
        """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("dateStart", Timestamp.valueOf(request.getDateStart()))
                .addValue("dateEnd", Timestamp.valueOf(request.getDateEnd()));

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(RoomEntity.class));

    }
}
