package com.example.securityex.controller;

import com.example.securityex.model.User;
import com.example.securityex.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(controllers = {UserController.class})
@WebAppConfiguration
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldGetUserByEmail() throws Exception {
        when(userRepository.findById(anyString()))
                .thenReturn(of(User.builder().email("user@gmail.com").password("pass").role("role1").build()));
        mockMvc.perform(get("/users").param("email", "user@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.password").value("pass"))
                .andExpect(jsonPath("$.role").value("role1"));
    }

    @Test
    public void shouldFailIfEmailNotExist() throws Exception {
        when(userRepository.findById(anyString())).thenReturn(empty());
        mockMvc.perform(get("/users").param("email", "user@gmail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateUser() throws Exception {
        when(userRepository.findById(anyString())).thenReturn(empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(passwordEncoder.encode(any())).thenReturn("encodedpass");
        mockMvc.perform(post("/users").content(
                "{\"email\":\"newemail@gmail.com\", \"password\":\"pass\",\"role\":\"role1\"}")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@gmail.com"))
                .andExpect(jsonPath("$.password").value("encodedpass"))
                .andExpect(jsonPath("$.role").value("role1"));
    }

    @Test
    public void shouldFailIfUserEmailAlreadyExist() throws Exception {
        when(userRepository.findById(anyString())).thenReturn(of(User.builder().build()));
        mockMvc.perform(post("/users").content(
                "{\"email\":\"newemail@gmail.com\", \"password\":\"pass\",\"role\":\"role1\"}")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}