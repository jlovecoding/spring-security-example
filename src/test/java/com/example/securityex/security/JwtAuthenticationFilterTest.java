package com.example.securityex.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserDetails userDetails;

    @Test
    public void shouldAuthorizeUser() throws ServletException, IOException {
        //Given
        when(httpServletRequest.getHeader(eq("Authorization"))).thenReturn("validjwt");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUserEmailFromJwt(anyString())).thenReturn("useremail");
        when(jwtTokenProvider.generateToken(any())).thenReturn("newjwt");
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "tokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "customUserDetailsService",
                customUserDetailsService);

        //When
        jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(httpServletResponse, times(1))
                .setHeader(eq("Authorization"), eq("newjwt"));
        verify(jwtTokenProvider, times(1)).generateToken(any());
        verify(jwtTokenProvider, times(1)).validateToken(anyString());
        verify(jwtTokenProvider, times(1)).getUserEmailFromJwt(anyString());
        verify(customUserDetailsService, times(1)).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void shouldInvalidateToken() throws IOException, ServletException {
        //Given
        when(httpServletRequest.getHeader(eq("Authorization"))).thenReturn("anyjwt");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(false);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "tokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "customUserDetailsService",
                customUserDetailsService);
        //When
        jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //Then
        verify(jwtTokenProvider, never()).getUserEmailFromJwt(anyString());
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(httpServletResponse, never()).setHeader(anyString(), anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(any(), any());
    }
}