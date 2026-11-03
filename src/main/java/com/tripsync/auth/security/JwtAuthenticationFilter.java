package com.tripsync.auth.security;

import com.tripsync.auth.service.CustomUserDetailService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private  final CustomUserDetailService userDetailService;

    protected  void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader==null||!authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token = authorizationHeader.substring(7);
        try {
            String email = jwtService.extractUsername(token);
            if (email!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                if (jwtService.isTokenValid(token,userDetails.getUsername())&& userDetails.isEnabled()){
                    UsernamePasswordAuthenticationToken authentication= new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        }catch (JwtException | IllegalArgumentException ex){
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request,response);
    }
}
