package com.springboot.blog.service;

import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
