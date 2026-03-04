create index if not exists index_booking_room_id on booking(room_id);
create index if not exists index_booking_guest_booking_id on booking_guest(booking_id);
create index if not exists index_booking_guest_guest_id on booking_guest(guest_id);
create index if not exists index_booking_date on booking(date_start, date_end);
