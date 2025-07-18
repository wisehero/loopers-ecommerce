package com.loopers.infrastructure.user;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public User save(User user) {
		return userJpaRepository.save(user);
	}

	@Override
	public Optional<User> findByUserId(LoginId loginId) {
		return userJpaRepository.findByUserId(loginId);
	}
}
