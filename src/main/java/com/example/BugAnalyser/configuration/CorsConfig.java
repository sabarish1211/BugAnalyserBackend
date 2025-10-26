// package com.example.BugAnalyser.configuration;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

// import java.util.List;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration cors = new CorsConfiguration();
//         cors.setAllowCredentials(true);
//         cors.setAllowedOriginPatterns(List.of("http://localhost:5173","*.vercel.app")); // ✅ frontend port
//         cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
//         cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", cors);
//         return new CorsFilter(source);
//     }
// }
