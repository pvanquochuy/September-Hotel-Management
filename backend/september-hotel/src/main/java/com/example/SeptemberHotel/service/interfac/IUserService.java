package com.example.SeptemberHotel.service.interfac;

import com.example.SeptemberHotel.dto.LoginRequest;
import com.example.SeptemberHotel.dto.Response;
import com.example.SeptemberHotel.entity.User;

public interface IUserService {
    Response register(User loginRequest);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);
}
