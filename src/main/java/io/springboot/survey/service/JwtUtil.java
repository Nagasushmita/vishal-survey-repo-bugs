package io.springboot.survey.service;

import io.springboot.survey.models.UserModel;
import org.springframework.stereotype.Service;


@Service
public interface JwtUtil {
     String generateToken(UserModel userModel);
     String extractUserEmail(String token);
}