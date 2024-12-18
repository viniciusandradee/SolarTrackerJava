package com.SolarTracker.service;

import com.SolarTracker.dto.RegisterDTO;
import com.SolarTracker.dto.UserDTO;
import com.SolarTracker.exception.ResourceNotFoundException;
import com.SolarTracker.model.User;
import com.SolarTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageProducer messageProducer;

    public User registerUser(RegisterDTO registerDTO) {
        Optional<User> existingUser = userRepository.findByEmail(registerDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email já registrado.");
        }

        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // Criptografa a senha
        User savedUser = userRepository.save(user);

        String message = "Novo usuário registrado: " + savedUser.getEmail();
        messageProducer.sendMessage(message);

        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
        }
        userRepository.deleteById(id);

        String message = "Usuário com ID " + id + " foi deletado.";
        messageProducer.sendMessage(message);
    }
}
