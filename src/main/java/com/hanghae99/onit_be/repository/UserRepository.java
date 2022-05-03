package com.hanghae99.onit_be.repository;

import com.hanghae99.onit_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByKakaoId(Long kakaoId);
    boolean existsByUserNickname(String userNickname);
    boolean existsByUsername(String username);

}
