package com.experiment.booking.service;

import com.experiment.booking.dto.CreateBookingRequest;
import com.experiment.booking.entity.Booking;
import com.experiment.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    /**
     * Baseline implementation for the experiment.
     *
     * This performs a check followed by an insert. The check and insert are not
     * an atomic reservation operation. The database unique constraint remains
     * the final integrity guard, so duplicate rows cannot be persisted.
     *
     * The independent stress probe is intended to explore races around this
     * flow and the resulting error handling. Keep this baseline unchanged until
     * the initial evidence has been captured.
     */
    @Transactional
    public Booking create(CreateBookingRequest request) {
        if (repository.existsByEventIdAndSeatId(request.eventId(), request.seatId())) {
            throw new SeatAlreadyBookedException(request.eventId(), request.seatId());
        }

        return repository.saveAndFlush(
                new Booking(request.eventId(), request.seatId(), request.customerId())
        );
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Booking findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }
}
