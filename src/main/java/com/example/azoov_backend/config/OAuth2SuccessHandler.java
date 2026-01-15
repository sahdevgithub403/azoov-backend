package com.example.azoov_backend.config;

import com.example.azoov_backend.model.Role;
import com.example.azoov_backend.model.User;
import com.example.azoov_backend.repository.UserRepository;
import com.example.azoov_backend.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setRole(Role.ADMIN); // Default to ADMIN for new signups
            user.setActive(true);
            user.setPassword(""); // No password for OAuth users
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user);

        // Redirect to Frontend with Token
        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}
