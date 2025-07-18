package com.loopers.infrastructure.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	Optional<User> findByLoginId(LoginId loginId);
}
