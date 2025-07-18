package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.domain.user.dto.UserCommand;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.IntegrationTestSupport;

class UserServiceIntegrationTest extends IntegrationTestSupport {

	@Autowired
	private UserService userService;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@MockitoSpyBean
	private UserRepository userRepository;

	@DisplayName("사용자를 생성할 때,")
	@Nested
	class CreateUser {

		@DisplayName("유효한 command를 받으면, repository의 save가 호출되고 사용자가 저장된다.")
		@Test
		void callsSaveAndCreateUser_whenCommandIsValid() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"testUser",
				"test.user@example.com",
				"MALE",
				"1996-05-04");

			// when
			userService.createUser(command);

			// then
			verify(userRepository, times(1)).save(any(User.class));

			User findUser = userJpaRepository.findByUserId(LoginId.of("testUser")).get();
			assertAll(
				() -> assertThat(findUser.getLoginId().value()).isEqualTo("testUser"),
				() -> assertThat(findUser.getEmail().getEmailAddress()).isEqualTo("test.user@example.com"),
				() -> assertThat(findUser.getGender().name()).isEqualTo("MALE"),
				() -> assertThat(findUser.getBirthDate().toString()).isEqualTo("1996-05-04")
			);
		}
	}

	@DisplayName("사용자를 ID로 조회할 때, ")
	@Nested
	class findByLoginId {

		@DisplayName("사용자가 존재하면, repository의 findByUserId가 호출되고 User 객체를 반환한다.")
		@Test
		void callsFindByUserIdAndReturnsUser_whenUserExists() {
			// given
			User saveduser = userService.createUser(new UserCommand.Create(
				"existing", "exist@example.com", "FEMALE", "1996-05-04"
			));
			LoginId loginIdToFind = saveduser.getLoginId();

			// when
			Optional<User> result = userService.findByUserId(loginIdToFind);

			// then
			verify(userRepository, times(1)).findByUserId(loginIdToFind);
			assertAll(
				() -> assertThat(result).isPresent(),
				() -> assertThat(result.get().getLoginId()).isEqualTo(loginIdToFind)
			);
		}
	}
}
