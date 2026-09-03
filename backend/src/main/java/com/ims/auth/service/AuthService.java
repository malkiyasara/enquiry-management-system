package com.ims.auth.service;


import com.ims.auth.dto.AuthResponse;
import com.ims.auth.dto.LoginRequest;
import com.ims.auth.dto.RegisterRequest;


public interface AuthService {


    AuthResponse register(
            RegisterRequest request
    );



    AuthResponse login(
            LoginRequest request
    );


}