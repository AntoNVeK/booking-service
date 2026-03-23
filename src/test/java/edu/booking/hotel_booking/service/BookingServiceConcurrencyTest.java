package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.BookingDao;
import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.dto.BookingToCreate;
import edu.booking.hotel_booking.dto.responses.BookingResponse;
import edu.booking.hotel_booking.entity.GuestEntity;
import edu.booking.hotel_booking.entity.RoomEntity;
import edu.booking.hotel_booking.util.BusyRoomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BookingService Concurrency Tests")
class BookingServiceConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID testRoomId;
    private UUID testGuestId1;
    private UUID testGuestId2;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        // Очищаем данные
        jdbcTemplate.execute("DELETE FROM booking_guest");
        jdbcTemplate.execute("DELETE FROM booking");
        jdbcTemplate.execute("DELETE FROM guest");
        jdbcTemplate.execute("DELETE FROM room");

        testRoomId = UUID.randomUUID();
        testGuestId1 = UUID.randomUUID();
        testGuestId2 = UUID.randomUUID();
        startTime = LocalDateTime.of(2025, 12, 25, 14, 0);
        endTime = LocalDateTime.of(2025, 12, 25, 16, 0);

        // Создаем комнату через прямой SQL, чтобы она сразу была доступна
        String insertRoomSql = """
            INSERT INTO room (id, floor, number_room, amount)
            VALUES (?, ?, ?, ?)
        """;
        jdbcTemplate.update(insertRoomSql, testRoomId, 1, 101, 2);

        // Создаем гостей
        String insertGuestSql = """
            INSERT INTO guest (id, second_name, first_name, birthday, phone_number)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(insertGuestSql, testGuestId1, "Иванов", "Иван",
                LocalDateTime.of(1990, 1, 1, 0, 0), "+79991234567");
        jdbcTemplate.update(insertGuestSql, testGuestId2, "Петров", "Петр",
                LocalDateTime.of(1992, 5, 15, 0, 0), "+79997654321");
    }

    @Test
    @DisplayName("10 concurrent booking requests for same room and time should allow only one success")
    void addBooking_10ConcurrentRequestsForSameSlot_ShouldAllowOnlyOne() throws InterruptedException {
        // Given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger unexpectedErrorCount = new AtomicInteger(0);

        List<UUID> guestIds = List.of(testGuestId1, testGuestId2);

        BookingToCreate request = new BookingToCreate(
                startTime,
                endTime,
                testRoomId,
                guestIds
        );

        // When
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    BookingResponse response = bookingService.addBooking(request);
                    if (response != null && "Бронь добавлена".equals(response.getMessage())) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (BusyRoomException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Unexpected error in thread " + Thread.currentThread().getName() + ": " + e.getMessage());
                    unexpectedErrorCount.incrementAndGet();
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed)
                .as("Все потоки должны завершиться за 15 секунд")
                .isTrue();

        assertThat(unexpectedErrorCount.get())
                .as("Не должно быть неожиданных ошибок")
                .isEqualTo(0);

        // Then
        assertThat(successCount.get())
                .as("Должно быть только одно успешное бронирование")
                .isEqualTo(1);

        assertThat(failureCount.get())
                .as("Остальные запросы должны получить BusyRoomException")
                .isEqualTo(threadCount - 1);

        // Проверяем, что в БД действительно только одно бронирование
        Integer bookingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM booking WHERE room_id = ?",
                Integer.class, testRoomId);
        assertThat(bookingCount)
                .as("В БД должна быть ровно одна запись бронирования")
                .isEqualTo(1);
    }
}