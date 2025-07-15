package com.loopers.domain.user;

import java.util.regex.Pattern;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Email {

	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
	private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

	private String emailAddress;

	private Email(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public static Email of(String emailAddress) {
		if (emailAddress == null || emailAddress.trim().isEmpty()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "이메일 주소는 비어 있을 수 없습니다.");
		}

		if (!PATTERN.matcher(emailAddress).matches()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 이메일 형식입니다. 입력값: " + emailAddress);
		}

		return new Email(emailAddress);
	}

	public String getEmailAddress() {
		return emailAddress;
	}
}
