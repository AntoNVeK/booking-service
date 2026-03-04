package edu.booking.hotel_booking.dao;


import edu.booking.hotel_booking.dto.BookingChangeRequest;
import edu.booking.hotel_booking.entity.BookingEntity;
import edu.booking.hotel_booking.entity.CreateBookingEntity;
import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.mappers.BookingRowMapper;
import edu.booking.hotel_booking.util.BookingNotFoundException;
import edu.booking.hotel_booking.util.BusyRoomException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public class BookingDao {


    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final BookingRowMapper bookingRowMapper;

    public BookingDao(NamedParameterJdbcTemplate jdbcTemplate, BookingRowMapper bookingRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookingRowMapper = bookingRowMapper;
    }

    public boolean isAvailable(UUID roomId, LocalDateTime dateStart, LocalDateTime dateEnd) {
        if (dateStart.isAfter(dateEnd) || dateStart.isEqual(dateEnd)) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты окончания");
        }

        String sql = """
            select 1 from booking
            where room_id = :roomId
            and date_start < :dateEnd
            and date_end > :dateStart
            limit 1
        """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId)
                .addValue("dateStart", Timestamp.valueOf(dateStart))
                .addValue("dateEnd", Timestamp.valueOf(dateEnd));

        return jdbcTemplate.queryForList(sql, params, Integer.class).isEmpty();
    }

    public void save(CreateBookingEntity booking) {
        try {
            insertBooking(booking);
            insertBookingGuests(booking.getId(), booking.getGuestList());
        }  catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Ошибка в данных: проверьте ID гостей", e);
        } catch (Exception e) {
            throw new BusyRoomException("Не хватает места комнате с id: " + booking.getRoomId());
        }

    }

    public Boolean checkExistById(UUID id) {
        String sql = """
                select exists( select 1 from booking where id = :id)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.queryForObject(sql, params, Boolean.class);

    }
    public BookingEntity findById(UUID id) {
        String sql = """
                select
                    booking.id,
                    booking.date_start,
                    booking.date_end,
                    room.id as room_id,
                    room.floor,
                    room.number_room,
                    room.amount
                from booking
                inner join room on booking.room_id = room.id
                where booking.id = :id
                """;


        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);


        try {
            BookingEntity booking = jdbcTemplate.queryForObject(sql, params, bookingRowMapper);
            if (booking != null) {
                booking.setGuestList(findGuestsByBookingId(id));
            }

            return booking;
        } catch (EmptyResultDataAccessException e) {
            throw new BookingNotFoundException("Бронь не найдена с id: " + id);
        }
    }



    private void insertBooking(CreateBookingEntity booking) {
        String sql = """
            insert into booking (id, date_start, date_end, room_id)
            values (:id, :dateStart, :dateEnd, :roomId)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", booking.getId())
                .addValue("dateStart", Timestamp.valueOf(booking.getDateStart()))
                .addValue("dateEnd", Timestamp.valueOf(booking.getDateEnd()))
                .addValue("roomId", booking.getRoomId());

        jdbcTemplate.update(sql, params);
    }

    private void insertBookingGuests(UUID bookingId, List<UUID> guestIds) {
        String sql = """
            insert into booking_guest (booking_id, guest_id)
            values (:bookingId, :guestId)
        """;

        // Используем batch update для множества гостей
        SqlParameterSource[] batch = guestIds.stream()
                        .map(guestId -> new MapSqlParameterSource()
                                .addValue("bookingId", bookingId)
                                .addValue("guestId", guestId)
                        )
                        .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batch);
    }

    private List<GuestEntity> findGuestsByBookingId(UUID bookingId) {
        String sql = """
                select
                    guest.id,
                    guest.second_name,
                    guest.first_name,
                    guest.birthday,
                    guest.phone_number
                from guest
                inner join booking_guest on guest.id = booking_guest.guest_id
                where booking_guest.booking_id = :bookingId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource("bookingId", bookingId);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(GuestEntity.class));
    }


    public void delete(UUID id) {
        String sql = """
                delete from booking
                where id = :id
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        int deleteRow = jdbcTemplate.update(sql, params);

        if (deleteRow == 0) {
            throw new BookingNotFoundException("Бронь с id " + id + " не найден");
        }
    }


    public boolean isAvailableExcludingCurrent(UUID roomId, LocalDateTime dateStart, LocalDateTime dateEnd, UUID excludeBookingId) {
        if (dateStart.isAfter(dateEnd) || dateStart.isEqual(dateEnd)) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты окончания");
        }

        String sql = """
            select 1 from booking
            where room_id = :roomId
            and id != :excludeBookingId
            and date_start < :dateEnd
            and date_end > :dateStart
            limit 1
        """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId)
                .addValue("excludeBookingId", excludeBookingId)
                .addValue("dateStart", Timestamp.valueOf(dateStart))
                .addValue("dateEnd", Timestamp.valueOf(dateEnd));

        return jdbcTemplate.queryForList(sql, params, Integer.class).isEmpty();
    }



    public void update(UUID id, BookingChangeRequest request) {
        try {
        updateBookingInfo(id, request);

        if (request.getGuestIds() != null) {
            deleteBookingGuests(id);
            insertBookingGuests(id, request.getGuestIds());
        }


        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Ошибка в данных: проверьте ID гостей", e);
        } catch (Exception e) {
            throw new BusyRoomException("Не хватает места комнате с id: " + request.getRoomId());
        }
    }


    private void updateBookingInfo(UUID id, BookingChangeRequest request) {
        String sql = """
            update booking
            set date_start = :dateStart,
                date_end = :dateEnd,
                room_id = :roomId
            where id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("dateStart", Timestamp.valueOf(request.getDateStart()))
                .addValue("dateEnd", Timestamp.valueOf(request.getDateEnd()))
                .addValue("roomId", request.getRoomId());


        try {
            int updatedRows = jdbcTemplate.update(sql, params);
            if (updatedRows == 0) {
                throw new BookingNotFoundException("Бронь с id " + id + " не найдена для обновления");
            }
        } catch (Exception e) {
            throw new BusyRoomException("Не хватает места комнате с id: " + request.getRoomId());
        }

    }


    private void deleteBookingGuests(UUID bookingId) {
        String sql = """
            delete from booking_guest
            where booking_id = :bookingId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookingId", bookingId);

        jdbcTemplate.update(sql, params);
    }

}
