package com.loopers.application.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFacade {

	private final UserService userService;

	@Transactional
	public UserInfo signUp(UserCommand.Create command) {
		if (userService.findByUserId(LoginId.of(command.userId())).isPresent()) {
			throw new CoreException(ErrorType.CONFLICT, "해당 ID를 가진 사용자가 이미 존재합니다. 입력값: " + command.userId());
		}

		User user = userService.createUser(command);
		return UserInfo.from(user);
	}

	public UserInfo getUserInfo(String userId) {
		return userService.findByUserId(LoginId.of(userId))
			.map(UserInfo::from)
			.orElse(null);
	}
}
