package com.example.SeptemberHotel.service.impl;

import com.example.SeptemberHotel.dto.LoginRequest;
import com.example.SeptemberHotel.dto.Response;
import com.example.SeptemberHotel.dto.UserDTO;
import com.example.SeptemberHotel.entity.User;
import com.example.SeptemberHotel.exception.OurException;
import com.example.SeptemberHotel.repository.UserRepository;
import com.example.SeptemberHotel.service.interfac.IUserService;
import com.example.SeptemberHotel.utils.JWTUtils;
import com.example.SeptemberHotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try{
            if(user.getRole() == null || user.getRole().isBlank()){
                user.setRole("USER");
            }
            if(userRepository.existsByEmail(user.getEmail())){
                throw new OurException(user.getEmail() + " is already");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setUser(userDTO);

        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occured During user registration " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {

        Response response = new Response();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new OurException("user not found"));

            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");
            response.setMessage("successful");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occured During user login " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        try {
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserList(userDTOList);
        }catch (OurException e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return null;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();

        try{
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return null;
    }

    @Override
    public Response deleteUser(String userId) {

        Response response = new Response();

        try{
            userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User not found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("successful");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {

        Response response = new Response();

        try{
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {

        Response response = new Response();

        try{
            User user = userRepository.findByEmail(email).orElseThrow(()-> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }
}
