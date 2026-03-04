
CREATE TABLE IF NOT EXISTS guest
(
    id uuid primary key not null,
    second_name varchar(128) not null,
    first_name varchar(128) not null,
    birthday date not null,
    phone_number varchar(32) not null unique
);

CREATE TABLE IF NOT EXISTS room
(
    id uuid primary key not null,
    floor integer not null,
    number_room integer not null,
    amount integer not null,
    constraint unique_floor_number unique (floor, number_room)
);

CREATE TABLE IF NOT EXISTS booking
(
    id uuid primary key not null,
    date_start timestamp not null,
    date_end timestamp not null,
    room_id uuid not null references room(id) on delete cascade
);

CREATE TABLE IF NOT EXISTS booking_guest
(
    booking_id uuid not null references booking(id) on delete cascade,
    guest_id uuid not null references guest(id) on delete cascade,
    primary key (booking_id, guest_id)
);


create or replace function check_room_capacity()
returns trigger
language plpgsql
as $$
declare
    max_guests integer;
begin
    select max(guest_count) into max_guests
    from (
        select count(*) as guest_count
        from booking
        inner join booking_guest on booking_guest.booking_id = booking.id
        where booking.room_id = new.id
        group by booking.id
    ) countguest;

    if max_guests is not null and max_guests > new.amount then
        raise exception
          'нельзя уменьшить количество мест: есть бронирование с % гостями',
          max_guests;
    end if;

    return new;
end;
$$;


create trigger trigger_check_room_capacity
before update of amount on room
for each row
execute function check_room_capacity();





create or replace function check_room_capacity_booking_guest_insert()
returns trigger
language plpgsql
as $$
declare
    room_capacity integer;
    guests_count integer;
begin

    select amount into room_capacity
    from room
    where id = (select room_id from booking where id = new.booking_id);


    select count(*) + 1 into guests_count
    from booking_guest
    where booking_id = new.booking_id;


    if guests_count > room_capacity then
        raise exception 'количество гостей % превышает вместимость комнаты %', guests_count, room_capacity;
    end if;

    return new;
end;
$$;


create trigger trigger_booking_guest_insert
before insert on booking_guest
for each row
execute function check_room_capacity_booking_guest_insert();


create or replace function check_room_capacity_booking_update_capacity()
returns trigger
language plpgsql
as $$
declare
    room_capacity integer;
    guests_count integer;
begin

    select amount into room_capacity
    from room
    where id = new.room_id;

    select count(*) into guests_count
    from booking_guest
    where booking_id = new.id;


    if guests_count > room_capacity then
        raise exception 'нельзя перенести бронь: в брони % гостей, а новая комната вмещает только %', guests_count, room_capacity;
    end if;

    return new;
end;
$$;


create trigger trigger_booking_update_capacity
before update of room_id on booking
for each row
execute function check_room_capacity_booking_update_capacity();


