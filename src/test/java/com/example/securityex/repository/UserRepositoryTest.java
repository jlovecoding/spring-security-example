package com.example.securityex.repository;

import com.example.securityex.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void shouldFindAll() {
        //Given
        Stream.of(1, 2).map(i -> User.builder().email("email" + i).password("pass" + i).role("role" + i).build())
                .forEach(testEntityManager::persistAndFlush);
        //When
        List<User> users = userRepository.findAll();
        //Then
        assertThat(users).hasSize(2);
        assertThat(users).containsExactlyInAnyOrder(
                User.builder().email("email1").password("pass1").role("role1").build(),
                User.builder().email("email2").password("pass2").role("role2").build());
    }

    @Test
    public void shouldSave() {
        //Given
        User newUser = User.builder().email("email1").password("pass1").role("role1").build();
        //When
        User saved = userRepository.save(newUser);
        //Then
        assertThat(saved).isEqualTo(newUser);
        assertThat(userRepository.findById("email1")).isPresent();
        assertThat(userRepository.findById("email1").get()).isEqualTo(saved);
    }
}