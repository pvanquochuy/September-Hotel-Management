package com.example.SeptemberHotel.service.impl;

import com.example.SeptemberHotel.dto.Response;
import com.example.SeptemberHotel.dto.RoomDTO;
import com.example.SeptemberHotel.entity.Room;
import com.example.SeptemberHotel.exception.OurException;
import com.example.SeptemberHotel.repository.RoomRepository;
import com.example.SeptemberHotel.service.AwsS3Service;
import com.example.SeptemberHotel.service.interfac.IRoomService;
import com.example.SeptemberHotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private AwsS3Service awsS3Service;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();
        try{
//            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            if(!photo.isEmpty()){
                byte[] photoBytes = photo.getBytes();
                Blob photoBlob = new SerialBlob(photoBytes);
                room.setPhoto(photoBlob);
            }
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);

            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);

            response.setStatusCode(200);
            response.setMessage("success");
            response.setRoom(roomDTO);
        }catch (OurException e){

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();
        try{
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);


            response.setStatusCode(200);
            response.setMessage("success");
            response.setRoomList(roomDTOList);
        }catch (OurException e){

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get all a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();
        try{
            roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room not found"));
            roomRepository.deleteById(roomId);

            response.setStatusCode(200);
            response.setMessage("success");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("success");
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error delete a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description , String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        Response response = new Response();
        try{

            Room room= roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room not found"));
            if(roomType != null) room.setRoomType(roomType);
            if(roomPrice != null) room.setRoomPrice(roomPrice);
            if(description != null) room.setRoomDescription(description);
            if(photoBytes != null  && photoBytes.length > 0){
                try{
                    room.setPhoto(new SerialBlob(photoBytes));
                }catch (SQLException e){
                    throw new OurException("Error updating room");
                }
            }

            Room updateRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updateRoom);

            response.setStatusCode(200);
            response.setMessage("success");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("success");
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error delete a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();
        try{
            Room room =  roomRepository.findById(roomId).orElseThrow(()-> new OurException("Room not found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);

            response.setStatusCode(200);
            response.setMessage("success");
            response.setRoom(roomDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("success");
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error find a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        Response response = new Response();
        try{
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);

            response.setStatusCode(200);
            response.setMessage("success");
            response.setRoomList(roomDTOList);
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error find a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRooms() {
        Response response = new Response();
        try{
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);


            response.setStatusCode(200);
            response.setMessage("success");
            response.setRoomList(roomDTOList);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("success");
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error find a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if(theRoom.isEmpty()){
            throw new OurException("Sorry, Room not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return new byte[0];
    }
}
