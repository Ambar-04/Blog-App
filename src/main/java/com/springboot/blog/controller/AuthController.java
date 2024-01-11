package com.springboot.blog.controller;


import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Build login REST API
    @PostMapping(value = {"/login","/sign-in"})
    private ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        String loginResponse = authService.login(loginDto);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    // Build register REST API
    @PostMapping(value = {"/register","/sign-up"})
    private ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String registerResponse = authService.register(registerDto);
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }
}
