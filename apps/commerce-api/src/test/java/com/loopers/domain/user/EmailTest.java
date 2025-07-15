package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class EmailTest {

	@DisplayName("Email 객체를 생성할 때")
	@Nested
	class Create {

		@DisplayName("유효한 이메일 주소를 입력하면 정상적으로 생성된다.")
		@Test
		void createSuccessfully_whenValidEmailIsProvided() {
			// given
			String validEmailAddress = "test.user@example.com";

			// when
			Email email = Email.of(validEmailAddress);

			// then
			assertAll(
				() -> assertThat(email).isNotNull(),
				() -> assertThat(email.getEmailAddress()).isEqualTo(validEmailAddress)
			);
		}

		@DisplayName("다양한 형식의 유효한 이메일 주소도 모두 정상적으로 생성된다.")
		@ParameterizedTest
		@ValueSource(strings = {
			"user123@example.com",
			"user.name@sub.example.co.kr",
			"user+alias@gmail.com",
			"user@example.io"
		})
		void createSuccessfully_forVariousValidEmailFormats(String validEmail) {
			// when
			Email eamil = Email.of(validEmail);

			// then
			assertThat(eamil.getEmailAddress()).isEqualTo(validEmail);
		}

		@DisplayName("null 또는 비어있거나 공백만 있는 문자열을 입력하면, BAD_REQUEST 예외가 발생한다.")
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {" ", "    "})
		void throwsException_whenEmailIsBlank(String blankEmail) {
			// when & then
			assertThatThrownBy(() -> Email.of(blankEmail))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("이메일 주소는 비어 있을 수 없습니다.");
		}

		@DisplayName("유효하지 않은 형식의 이메일 주소를 입력하면, BAD_REQUEST 예외가 발생한다.")
		@ParameterizedTest
		@ValueSource(strings = {
			"plainaddress",
			"#@%^%#$@#$@#.com",
			"@domain.com",
			"test.name@.com",
			"test.name@domain.com.",
			"test.name@domain..com",
			"test@domain",
			"test @domain.com"
		})
		void throwsException_whenInvalidEmailFormatIsProvided(String invalidEmail) {
			// when & then
			assertThatThrownBy(() -> Email.of(invalidEmail))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("유효하지 않은 이메일 형식입니다. 입력값: " + invalidEmail);
		}
	}
}
