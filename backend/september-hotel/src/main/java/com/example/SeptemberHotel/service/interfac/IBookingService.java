package com.example.SeptemberHotel.service.interfac;

import com.example.SeptemberHotel.dto.Response;
import com.example.SeptemberHotel.entity.Booking;

import java.util.Optional;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userid, Booking bookingRequest);

    Response  findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBookings(Long bookingId);
}
