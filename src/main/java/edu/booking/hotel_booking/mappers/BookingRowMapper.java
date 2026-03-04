package edu.booking.hotel_booking.mappers;

import edu.booking.hotel_booking.entity.BookingEntity;
import edu.booking.hotel_booking.entity.RoomEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class BookingRowMapper implements RowMapper<BookingEntity> {

    @Override
    public BookingEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookingEntity booking = new BookingEntity();
        booking.setId(rs.getObject("id", UUID.class));
        booking.setDateStart(rs.getTimestamp("date_start").toLocalDateTime());
        booking.setDateEnd(rs.getTimestamp("date_end").toLocalDateTime());

        RoomEntity room = new RoomEntity();
        room.setId(rs.getObject("room_id", UUID.class));
        room.setFloor(rs.getInt("floor"));
        room.setNumber(rs.getInt("number_room"));
        room.setAmount(rs.getInt("amount"));

        booking.setRoom(room);

        return booking;
    }
}