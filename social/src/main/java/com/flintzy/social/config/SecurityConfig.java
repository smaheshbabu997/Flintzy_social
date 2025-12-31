package com.flintzy.social.config;

import com.flintzy.social.services.OAuth2SuccessHandler;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final jwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
   SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
       http
               .csrf(csrf->csrf.disable())
               .authorizeHttpRequests(auth->auth
                       .requestMatchers("/actuator/**", "/api/auth/login", "/api/auth/refresh", "/api/facebook/callback").permitAll()
                       .anyRequest().authenticated())
               .sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .oauth2Login(oauth->oauth.successHandler((AuthenticationSuccessHandler) oAuth2SuccessHandler))
               .addFilterBefore((Filter) jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
       return http.build();
   }
   @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
   }

}
