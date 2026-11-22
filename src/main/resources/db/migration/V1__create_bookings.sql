CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    seat_id VARCHAR(30) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_booking_event_seat UNIQUE (event_id, seat_id)
);

CREATE INDEX idx_bookings_created_at ON bookings (created_at DESC);
