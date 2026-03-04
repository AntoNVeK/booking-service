package edu.booking.hotel_booking.dao;


import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.util.GuestAlreadyExistsException;
import edu.booking.hotel_booking.util.GuestNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class GuestDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GuestDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public GuestEntity findById(UUID id) {
        String sql = "select * from guest where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(sql, namedParameters, new BeanPropertyRowMapper<>(GuestEntity.class));
    }

    public void save(GuestEntity guest) {

        String sql = """
                insert into guest (id, second_name, first_name, birthday, phone_number)
                        values (:id, :secondName, :firstName, :birthday, :phoneNumber);
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", guest.getId())
                .addValue("secondName", guest.getSecondName())
                .addValue("firstName", guest.getFirstName())
                .addValue("birthday", guest.getBirthday())
                .addValue("phoneNumber", guest.getPhoneNumber());

        try {
            jdbcTemplate.update(sql, params);
        } catch (DuplicateKeyException e) {
            throw new GuestAlreadyExistsException("Гость с таким телефоном уже существует");
        }
    }


    public void update(GuestEntity guest) {
        String sql = """
                update guest
                set first_name = :firstName,
                    second_name = :secondName,
                    birthday = :birthday,
                    phone_number = :phoneNumber
                where id = :id
                """;

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", guest.getId())
                .addValue("firstName", guest.getFirstName())
                .addValue("secondName", guest.getSecondName())
                .addValue("birthday", guest.getBirthday())
                .addValue("phoneNumber", guest.getPhoneNumber());

        try {
            int updatedRows = jdbcTemplate.update(sql, params);
            if (updatedRows == 0) {
                throw new GuestNotFoundException("Гость с id " + guest.getId() + " не найден");
            }
        } catch (DuplicateKeyException e) {
            throw new GuestAlreadyExistsException("Гость с таким телефоном уже существует");
        }
    }

}

