package com.loopers.domain.user;

import java.util.stream.Stream;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum Gender {
	MALE, FEMALE;

	public static Gender fromInput(String value) {
		if (value == null || value.isBlank()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "성별은 필수 값입니다. 남성, 여성 중에 선택해주세요.");
		}

		return Stream.of(Gender.values())
			.filter(gender -> gender.name().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 성별 값입니다. 입력값: " + value));
	}
}
