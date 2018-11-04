package com.example.securityex.security;

import com.example.securityex.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {"app.jwtSecret=1234", "app.jwtExpirationSeconds=3600"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JwtTokenProvider.class)
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Test
    public void shouldGenerateToken() {
        //Given
        when(authentication.getPrincipal())
                .thenReturn(new UserPrincipal(User.builder().email("user@gmail.com").password("pass").role("anyrole")
                        .build()));
        //When
        String generatedToken = jwtTokenProvider.generateToken(authentication);
        //Then
        assertThat(generatedToken).isNotBlank();
    }

    @Test
    public void shouldValidateToken() {
        //Given
        when(authentication.getPrincipal())
                .thenReturn(new UserPrincipal(User.builder().email("user@gmail.com").password("pass").role("anyrole")
                        .build()));
        String generatedToken = jwtTokenProvider.generateToken(authentication);
        //When && Then
        assertThat(jwtTokenProvider.validateToken(generatedToken)).isTrue();
    }

    @Test
    public void shouldInvalidateToken() {
        //Given && When && Then
        assertThat(jwtTokenProvider.validateToken("invalidToken")).isFalse();
    }

}