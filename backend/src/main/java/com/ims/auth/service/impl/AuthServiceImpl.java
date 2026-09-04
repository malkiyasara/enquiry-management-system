package com.ims.auth.service.impl;


import com.ims.auth.dto.AuthResponse;
import com.ims.auth.dto.LoginRequest;
import com.ims.auth.dto.RegisterRequest;

import com.ims.auth.entity.Role;
import com.ims.auth.entity.User;

import com.ims.auth.enums.UserRole;

import com.ims.auth.repository.RoleRepository;
import com.ims.auth.repository.UserRepository;

import com.ims.auth.service.AuthService;

import com.ims.security.CustomUserDetails;
import com.ims.security.JwtService;


import lombok.RequiredArgsConstructor;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Set;



@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {



    private final UserRepository userRepository;


    private final RoleRepository roleRepository;


    private final PasswordEncoder passwordEncoder;


    private final AuthenticationManager authenticationManager;


    private final JwtService jwtService;





    @Override
    public AuthResponse register(
            RegisterRequest request
    ) {



        // Check existing email

        if(userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );

        }




        // Get Role

        UserRole selectedRole =
                request.getRole() != null
                        ?
                        request.getRole()
                        :
                        UserRole.USER;



        Role role =
                roleRepository.findByName(selectedRole)
                        .orElseThrow(
                                () ->
                                new RuntimeException(
                                "Role not found"
                                )
                        );





        // Create User

        User user =
                User.builder()

                        .firstName(
                                request.getFirstName()
                        )

                        .lastName(
                                request.getLastName()
                        )

                        .email(
                                request.getEmail()
                        )

                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )

                        .phone(
                                request.getPhone()
                        )

                        .roles(
                                new HashSet<>(
                                        Set.of(role)
                                )
                        )

                        .build();




        User savedUser =
                userRepository.save(user);



        return mapToResponse(savedUser);

    }







    @Override
    public AuthResponse login(
            LoginRequest request
    ) {



        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),

                        request.getPassword()

                )

        );




        User user =
                userRepository.findByEmail(
                        request.getEmail()
                )
                .orElseThrow(
                        () ->
                        new RuntimeException(
                        "User not found"
                        )
                );




        CustomUserDetails userDetails =
                new CustomUserDetails(user);




        String token =
                jwtService.generateToken(
                        userDetails
                );




        AuthResponse response =
                mapToResponse(user);



        response.setToken(token);



        return response;

    }








    private AuthResponse mapToResponse(
            User user
    ) {


        String role =
                user.getRoles()
                        .stream()
                        .findFirst()
                        .map(
                                r ->
                                r.getName().name()
                        )
                        .orElse(
                                "USER"
                        );



        return AuthResponse.builder()

                .id(
                        user.getId()
                )

                .firstName(
                        user.getFirstName()
                )

                .lastName(
                        user.getLastName()
                )

                .email(
                        user.getEmail()
                )

                .role(
                        role
                )

                .build();

    }

}