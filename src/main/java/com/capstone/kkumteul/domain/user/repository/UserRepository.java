package com.capstone.kkumteul.domain.user.repository;

import com.capstone.kkumteul.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String username);

    // User의 ID는 unique이므로, 중복 검증할 때 사용할 메소드
    Boolean existsByUserId(String userId);
}
