package com.experiment.booking.repository;

import com.experiment.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByEventIdAndSeatId(String eventId, String seatId);
    Optional<Booking> findByEventIdAndSeatId(String eventId, String seatId);
    List<Booking> findAllByOrderByCreatedAtDesc();
}
