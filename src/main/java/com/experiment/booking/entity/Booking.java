package com.experiment.booking.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_booking_event_seat", columnNames = {"event_id", "seat_id"})
})
public class Booking {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "event_id", nullable = false, length = 100)
    private String eventId;

    @Column(name = "seat_id", nullable = false, length = 30)
    private String seatId;

    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Booking() {}

    public Booking(String eventId, String seatId, String customerId) {
        this.eventId = eventId;
        this.seatId = seatId;
        this.customerId = customerId;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getEventId() { return eventId; }
    public String getSeatId() { return seatId; }
    public String getCustomerId() { return customerId; }
    public Instant getCreatedAt() { return createdAt; }
}
