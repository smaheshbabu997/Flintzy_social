package com.flintzy.social.services;

import com.flintzy.social.auth.JwtUtil;
import com.flintzy.social.entities.User;
import com.flintzy.social.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) oAuth2Token.getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String name = (String) principal.getAttributes().get("name");
        String providerId = (String) principal.getAttributes().get("sub");
        User user = userRepo.findByEmail(email)
                .orElseGet(() -> userRepo.save(User.builder()
                        .email(email)
                        .name(name)
                        .provider("GOOGLE")
                        .provider_id(providerId)
                        .role("ROLE_USER")
                        .build()));
        String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), List.of("ROLE_USER"));
        response.setContentType("text/html");
        response.getWriter().write("<html><center><body><h1>Login Successful</h1>" +
                "<p>Copy your JWT token below for Postman:</p>" +
                "<textarea style='width:100%; height:100px;'>" + jwt + "</textarea>" +
                "</body></center></html>");
    }
}
