package com.example.BugAnalyser.configuration;

import com.example.BugAnalyser.service.JwtUserDetailsService;
import com.example.BugAnalyser.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String extractToken(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

   @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();

    // ✅ Skip JWT validation for public endpoints
    if (path.startsWith("/api/projects")) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = extractToken(request);

    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    request.setAttribute("username",username);
                    System.out.println("Authenticated user: " + username);
                    System.out.println("Authorities: " + userDetails.getAuthorities());
                }
            }
        } catch (io.jsonwebtoken.JwtException ex) {
            // Token invalid or expired -> let security handle unauthenticated requests
        }
    }

    filterChain.doFilter(request, response);
}

}
