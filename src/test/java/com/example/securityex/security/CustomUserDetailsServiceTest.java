package com.example.securityex.security;

import com.example.securityex.model.User;
import com.example.securityex.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void shouldLoadUserByUsername() {
        //Given
        User user = User.builder().email("email1").password("pass1").role("role1").build();
        when(userRepository.findById(anyString())).thenReturn(of(user));
        //When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("email1");
        //Then
        verify(userRepository, times(1)).findById(anyString());
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo(user.getRole());
    }

    @Test
    public void shouldNotFindUser() {
        //Given
        when(userRepository.findById(anyString())).thenReturn(empty());
        //When && Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("email1"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with username or email : email1");
        verify(userRepository, times(1)).findById(anyString());
    }
}