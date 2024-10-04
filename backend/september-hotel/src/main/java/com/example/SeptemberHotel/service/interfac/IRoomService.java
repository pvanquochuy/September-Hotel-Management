package com.example.SeptemberHotel.service.interfac;

import com.example.SeptemberHotel.dto.Response;
import com.example.SeptemberHotel.entity.Room;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IRoomService {

    Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description);

    List<String> getAllRoomTypes();

    Response getAllRooms();

    Response deleteRoom(Long roomId);

    Response updateRoom(Long roomId, String description , String roomType, BigDecimal roomPrice, byte[] photoBytes);

    Response getRoomById(Long roomId);

    Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    Response getAvailableRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;
}
