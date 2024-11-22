package com.SolarTracker.controller;

import com.SolarTracker.dto.LoginDTO;
import com.SolarTracker.dto.RegisterDTO;
import com.SolarTracker.dto.UserDTO;
import com.SolarTracker.exception.ResourceNotFoundException;
import com.SolarTracker.model.User;
import com.SolarTracker.service.UserService;
import com.SolarTracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        try {
            if (registerDTO.getEmail() == null || registerDTO.getPassword() == null) {
                throw new IllegalArgumentException("Email e senha são obrigatórios.");
            }
            userService.registerUser(registerDTO);
            return ResponseEntity.ok("Usuário registrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao registrar o usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            if (loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
                throw new IllegalArgumentException("Email e senha são obrigatórios.");
            }

            User user = userService.findByEmail(loginDTO.getEmail());
            if (user == null) {
                throw new ResourceNotFoundException("Usuário não encontrado.");
            }

            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Credenciais inválidas!");
            }

            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(token);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao fazer login: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.findAllUsers();
            if (users.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum usuário encontrado.");
            }
            return ResponseEntity.ok(users);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("ID inválido.");
            }
            userService.deleteUserById(id);
            return ResponseEntity.ok("Usuário com ID " + id + " deletado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao deletar usuário: " + e.getMessage());
        }
    }
}