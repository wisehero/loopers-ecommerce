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
public class UserId {

	private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");

	private String userId;

	private UserId(String userId) {
		this.userId = userId;
	}

	public static UserId of(String userId) {
		if (userId == null || !PATTERN.matcher(userId).matches()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 영문 및 숫자로 구성된 10자 이내여야 합니다.");
		}
		return new UserId(userId);
	}

	public String value() {
		return userId;
	}
}
