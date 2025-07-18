package com.loopers.domain.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.loopers.domain.user.dto.UserCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User createUser(UserCommand.Create command) {
		User user = User.create(command);
		return userRepository.save(user);
	}

	public Optional<User> findByUserId(LoginId loginId) {
		return userRepository.findByLoginId(loginId);
	}
}
