package com.example.BugAnalyser.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                // allow common local dev origins (any localhost port) and 127.0.0.1
                // Using allowedOriginPatterns lets us accept http://localhost:5173, :5174, etc.
                config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*","*.vercel.app"));
                // Allow all methods used by the app during development
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
                // Allow common headers and let the browser send other headers during dev
                config.setAllowedHeaders(List.of("*"));
                config.setExposedHeaders(List.of("Authorization", "Content-Type"));
                config.setAllowCredentials(true);
                // Cache preflight responses for 1 hour for dev speed
                config.setMaxAge(3600L);
                return config;
            }))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .authorizeHttpRequests(auth -> auth
            // allow preflight requests for all endpoints
            .requestMatchers(HttpMethod.OPTIONS).permitAll()
            // auth endpoints are public
            .requestMatchers("/auth/login","/auth/register").permitAll()
            // permit both the exact projects path and any subpaths
            .requestMatchers("/projects", "/projects/**").permitAll()
            .requestMatchers("/users/**").hasRole("ADMIN")
            .requestMatchers("/teams/**").hasAnyRole("PM", "ADMIN")
            .requestMatchers("/api/bugs/**").hasAnyRole("DEVELOPER", "TESTER", "PM", "ADMIN")
            .requestMatchers("/api/manager/**").hasRole("MANAGER")
            .anyRequest().authenticated()
        )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
        // System.out.println("Security filter chain configured",roles);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
