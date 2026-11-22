package com.experiment.booking.service;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(String eventId, String seatId) {
        super("Seat " + seatId + " is already booked for event " + eventId);
    }
}
