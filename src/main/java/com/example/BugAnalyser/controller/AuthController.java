package com.example.BugAnalyser.controller;

import com.example.BugAnalyser.dto.UserDTO;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.model.JwtResponse;
import com.example.BugAnalyser.payload.LoginRequest;
import com.example.BugAnalyser.payload.RegisterRequest;
import com.example.BugAnalyser.service.JwtUserDetailsService;
import com.example.BugAnalyser.service.UserService;
import com.example.BugAnalyser.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() == null ? "DEVELOPER" : request.getRole().toUpperCase());
        user.setIsActive(true);

        User created = userService.createUser(user);

        // Convert to DTO before sending
        UserDTO dto = new UserDTO(
                created.getUserId(),
                created.getUsername(),
                created.getEmail(),
                created.getRole(),
                created.getIsActive(),
                created.getTeam()
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);

        // fetch user's role to include in the response
        String role = userService.getUserByUsername(request.getUsername())
            .map(User::getRole)
            .orElse(null);

        return ResponseEntity.ok(new JwtResponse(token, role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body("User disabled");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            System.out.println("Unauthenticated access to /me");
            return ResponseEntity.status(401).body("Unauthenticated");
        }

        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .map(user -> {
                    // Convert to DTO
                    UserDTO dto = new UserDTO(
                            user.getUserId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole(),
                            user.getIsActive(),
                            user.getTeam()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
