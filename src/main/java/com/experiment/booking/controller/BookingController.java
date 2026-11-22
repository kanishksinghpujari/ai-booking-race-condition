package com.experiment.booking.controller;

import com.experiment.booking.dto.BookingResponse;
import com.experiment.booking.dto.CreateBookingRequest;
import com.experiment.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        return BookingResponse.from(service.create(request));
    }

    @GetMapping
    public List<BookingResponse> findAll() {
        return service.findAll().stream().map(BookingResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable UUID id) {
        return BookingResponse.from(service.findById(id));
    }
}
