package com.loopers.infrastructure.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserId;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserId(UserId userId);
}
