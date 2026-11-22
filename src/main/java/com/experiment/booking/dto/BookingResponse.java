package com.experiment.booking.dto;

import com.experiment.booking.entity.Booking;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        String eventId,
        String seatId,
        String customerId,
        Instant createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getEventId(),
                booking.getSeatId(),
                booking.getCustomerId(),
                booking.getCreatedAt()
        );
    }
}
