package com.SolarTracker.config;

import com.SolarTracker.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtém o cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");

        // Verifica se o cabeçalho contém o token no formato "Bearer {token}"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " do início

            // Extrai o email do token
            String email = jwtUtil.extractEmail(token);

            // Se o email não for nulo e a autenticação ainda não foi realizada
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Valida o token
                if (jwtUtil.validateToken(token)) {
                    // Cria um objeto de autenticação com o email extraído
                    User user = new User(email, "", new ArrayList<>());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    // Define os detalhes de autenticação (detalhes da requisição)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Continua o filtro da cadeia
        filterChain.doFilter(request, response);
    }
}
