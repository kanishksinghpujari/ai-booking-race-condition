package com.experiment.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBookingRequest(
        @NotBlank @Size(max = 100) String eventId,
        @NotBlank @Size(max = 30) String seatId,
        @NotBlank @Size(max = 100) String customerId
) {}
