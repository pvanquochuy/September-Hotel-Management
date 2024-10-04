package com.example.SeptemberHotel.dto;


import com.example.SeptemberHotel.entity.Booking;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {

    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private Blob photo;
    private String roomDescription;
    private List<BookingDTO> bookings;
}
