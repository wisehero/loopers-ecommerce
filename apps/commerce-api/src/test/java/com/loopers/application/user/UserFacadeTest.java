package com.loopers.application.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.support.IntegrationTestSupport;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class UserFacadeTest extends IntegrationTestSupport {

	@Autowired
	private UserFacade userFacade;

	@MockitoSpyBean
	private UserRepository userRepository;

	@DisplayName("회원가입시,")
	@Nested
	class SignUp {
		@DisplayName("회원가입에 필요한 입력값이 유효하면, 회원 가입에 성공한다.")
		@Test
		void userIsSaved_whenSignUpRequestIsValid() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"newUser",
				"new.user@example.com",
				"FEMALE",
				"1998-03-14"
			);

			// when
			userFacade.signUp(command);

			// then
			verify(userRepository, times(1)).save(any(User.class));
		}

		@DisplayName("이미 가입된 ID로 회원가입을 시도하면 실패한다.")
		@Test
		void throwsBadRequestException_whenUserIdAlreadyExists() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"existing",
				"existing.user@example.com",
				"MALE",
				"1996-05-04"
			);
			userFacade.signUp(command);

			// when & then
			UserCommand.Create duplicateCommand = new UserCommand.Create(
				"existing",
				"duplicate.user@example",
				"MALE",
				"1996-05-04");

			assertThatThrownBy(() -> userFacade.signUp(duplicateCommand))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.CONFLICT)
				.hasMessage("해당 ID를 가진 사용자가 이미 존재합니다. 입력값: existing");
		}
	}

	@DisplayName("사용자 조회시,")
	@Nested
	class getUserInfo {
		@DisplayName("존재하는 사용자 ID로 조회하면, 해당 사용자의 정보를 반환한다.")
		@Test
		void returnsUserInfo_whenUserExists() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"existing",
				"existing.user@example.com",
				"MALE",
				"1996-05-04");
			userFacade.signUp(command);

			// when
			UserInfo result = userFacade.getUserInfo("existing");

			// then
			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.userId().getUserId()).isEqualTo("existing"),
				() -> assertThat(result.email().getEmailAddress()).isEqualTo("existing.user@example.com"),
				() -> assertThat(result.gender().name()).isEqualTo("MALE")
				, () -> assertThat(result.birthDate().toString()).isEqualTo("1996-05-04")
			);
		}

		@DisplayName("존재하지 않는 사용자 ID로 조회하면, null을 반환한다.")
		@Test
		void returnsNull_whenUserDoesNotExist() {
			// given
			String nonExistentUserId = "nonExist";

			// when
			UserInfo result = userFacade.getUserInfo(nonExistentUserId);

			// then
			assertThat(result).isNull();
		}
	}
}
