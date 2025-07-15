package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import com.loopers.domain.user.dto.UserCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class UserTest {

	@DisplayName("User 객체를 생성할 때")
	@Nested
	class Create {

		// MockedStatic 인스턴스를 필드로 선언
		private MockedStatic<UserId> mockUserId;
		private MockedStatic<Email> mockEmail;
		private MockedStatic<Gender> mockGender;

		@BeforeEach
		void setUp() {
			// 각 테스트 전에 정적 메서드 모킹을 활성화합니다.
			mockUserId = mockStatic(UserId.class);
			mockEmail = mockStatic(Email.class);
			mockGender = mockStatic(Gender.class);
		}

		@AfterEach
		void tearDown() {
			// 각 테스트 후에 모킹을 해제하여 다른 테스트에 영향을 주지 않도록 합니다.
			mockUserId.close();
			mockEmail.close();
			mockGender.close();
		}

		@DisplayName("모든 입력이 유효하면, User를 성공적으로 생성한다.")
		@Test
		void createUserSuccessfully_whenAllInputsAreValid() {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"testuser",
				"test@example.com",
				"MALE",
				"1996-05-04"
			);

			UserId fakeUserId = mock(UserId.class);
			when(fakeUserId.getUserId()).thenReturn("testuser");
			mockUserId.when(() -> UserId.of("testuser")).thenReturn(fakeUserId);

			Email fakeEmail = mock(Email.class);
			when(fakeEmail.getEmailAddress()).thenReturn("test@example.com");
			mockEmail.when(() -> Email.of("test@example.com")).thenReturn(fakeEmail);

			mockGender.when(() -> Gender.fromInput("MALE")).thenReturn(Gender.MALE);

			// when
			User user = User.create(command);

			// then
			mockUserId.verify(() -> UserId.of("testuser"), times(1));
			mockEmail.verify(() -> Email.of("test@example.com"), times(1));
			mockGender.verify(() -> Gender.fromInput("MALE"), times(1));

			assertAll(
				() -> assertThat(user).isNotNull(),
				() -> assertThat(user.getUserId().getUserId()).isEqualTo("testuser"),
				() -> assertThat(user.getEmail().getEmailAddress()).isEqualTo("test@example.com"),
				() -> assertThat(user.getGender()).isEqualTo(Gender.MALE),
				// birthDate는 모킹하지 않았으므로 실제 파싱 결과를 검증합니다.
				() -> assertThat(user.getBirthDate()).isEqualTo(LocalDate.parse("1996-05-04"))
			);
		}

		@DisplayName("생년월일 형식이 유효한 형식이 아니라면, BAD_REQUEST 예외가 발생한다.")
		@ParameterizedTest
		@ValueSource(strings = {"2000/01/15", "2000-1-15", "2000-01-32", "2000-01-15T00:00:00"})
		void throwException_whenBirthDateFormatIsInvalid(String invalidBirthDate) {
			// given
			UserCommand.Create command = new UserCommand.Create(
				"testuser",
				"test@example.com",
				"MALE",
				invalidBirthDate
			);

			// when & then
			assertThatThrownBy(() -> User.create(command))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("생년월일의 형식이 올바르지 않습니다. (yyyy-MM-dd)");
		}
	}

	@DisplayName("생년월일이 null이면, BAD_REQUEST 예외가 발생한다.")
	@Test
	void throwsExcetpion_whenBirthDateIsNull() {
		// given
		UserCommand.Create command = new UserCommand.Create(
			"testuser",
			"test@example.com",
			"FEMALE",
			null);

		// when & then
		assertThatThrownBy(() -> User.create(command))
			.isInstanceOf(CoreException.class)
			.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
			.hasMessage("생년월일은 비어 있을 수 없습니다.");
	}



}
