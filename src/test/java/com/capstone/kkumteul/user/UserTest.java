package com.capstone.kkumteul.user;

import com.capstone.kkumteul.domain.user.entity.Gender;
import com.capstone.kkumteul.domain.user.entity.User;
import com.capstone.kkumteul.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser() {
        User user = User.builder()
                .userId("test123")
                .password("test123")
                .username("test123")
                .gender(Gender.MALE)
                .role("USER")
                .build();

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("test123");
        assertThat(saved.getPassword()).isEqualTo("test123");
        assertThat(saved.getGender()).isEqualTo(Gender.MALE);
        assertThat(saved.getRole()).isEqualTo("USER");
    }

    @Test
    public void createUser_thenFindById() {
        // given
        User user = User.builder()
                .userId("test123")
                .password("test123")
                .username("test123")
                .gender(Gender.MALE)
                .role("USER")
                .build();

        User savedUser = userRepository.save(user);

        userRepository.flush();

        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getUserId()).isEqualTo("test123");
    }
}
