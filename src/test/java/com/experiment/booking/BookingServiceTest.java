package com.experiment.booking;

import com.experiment.booking.dto.CreateBookingRequest;
import com.experiment.booking.entity.Booking;
import com.experiment.booking.repository.BookingRepository;
import com.experiment.booking.service.BookingService;
import com.experiment.booking.service.SeatAlreadyBookedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {
    private BookingRepository repository;
    private BookingService service;

    @BeforeEach
    void setUp() {
        repository = mock(BookingRepository.class);
        service = new BookingService(repository);
    }

    @Test
    void createsBookingWhenSeatIsAvailable() {
        var request = new CreateBookingRequest("event-1", "A-1", "customer-1");
        when(repository.existsByEventIdAndSeatId("event-1", "A-1")).thenReturn(false);
        when(repository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.create(request);

        assertEquals("event-1", result.getEventId());
        assertEquals("A-1", result.getSeatId());
        assertEquals("customer-1", result.getCustomerId());
        verify(repository).saveAndFlush(any(Booking.class));
    }

    @Test
    void rejectsSeatThatIsAlreadyBooked() {
        var request = new CreateBookingRequest("event-1", "A-1", "customer-2");
        when(repository.existsByEventIdAndSeatId("event-1", "A-1")).thenReturn(true);

        assertThrows(SeatAlreadyBookedException.class, () -> service.create(request));

        verify(repository, never()).saveAndFlush(any(Booking.class));
    }

    @Test
    void allowsSameSeatForDifferentEvents() {
        var request = new CreateBookingRequest("event-2", "A-1", "customer-1");
        when(repository.existsByEventIdAndSeatId("event-2", "A-1")).thenReturn(false);
        when(repository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.create(request);

        assertEquals("event-2", result.getEventId());
        assertEquals("A-1", result.getSeatId());
    }
}
