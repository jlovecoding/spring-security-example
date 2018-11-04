package com.example.securityex.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String jwt = getJwtFromRequest(request);

        Optional<String> userEmail = getUserEmailFromJwt(jwt);

        Optional<UsernamePasswordAuthenticationToken> usernamePasswordAuthenticationToken =
                getUsernamePasswordAuthenticationToken(userEmail.orElse(null));

        usernamePasswordAuthenticationToken
                .ifPresent(u -> {
                    u.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(u);
                    response.setHeader("Authorization", tokenProvider.generateToken(u));
                });

        filterChain.doFilter(request, response);
    }

    private Optional<UsernamePasswordAuthenticationToken> getUsernamePasswordAuthenticationToken(
            final String userEmail) {
        return Optional.ofNullable(userEmail)
                .map(customUserDetailsService::loadUserByUsername)
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()));
    }

    private Optional<String> getUserEmailFromJwt(final String jwt) {
        return Optional.ofNullable(jwt)
                .filter(tokenProvider::validateToken)
                .map(tokenProvider::getUserEmailFromJwt);
    }

    private String getJwtFromRequest(final HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
