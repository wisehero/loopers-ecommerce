package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class UserIdTest {

	@DisplayName("UserId 객체를 생성할 때")
	@Nested
	class Create {

		@DisplayName("영문, 숫자로 구성된 10자 이내의 ID를 입력하면, 정상적으로 생성된다.")
		@ParameterizedTest
		@ValueSource(strings = {
			"testuser",
			"user123",
			"1234567890",
			"a",
			"abcdef1234"
		})
		void createSuccesfully_whenValidIdIsProvided(String validId) {
			// when
			UserId userId = UserId.of(validId);

			// then
			assertAll(
				() -> assertThat(userId).isNotNull(),
				() -> assertThat(userId.value()).isEqualTo(validId)
			);
		}

		@DisplayName("null을 입력하거나 유효하지 않은 형식의 ID를 입력하면, BAD_REQUEST 예외가 발생한다.")
		@ParameterizedTest
		@NullSource
		@ValueSource(strings = {
			"",
			" ",
			"12345678901",
			"user-id",
			"user id"
		})
		void throwsException_whenInvalidIdIsProvided(String invalidId) {
			// when & then
			assertThatThrownBy(() -> UserId.of(invalidId))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("사용자 ID는 영문 및 숫자로 구성된 10자 이내여야 합니다.");
		}
	}
}
