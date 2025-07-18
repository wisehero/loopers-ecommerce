package com.loopers.domain.user.dto;

import java.time.LocalDate;

import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;

public record UserInfo(
	LoginId loginId,
	Email email,
	Gender gender,
	LocalDate birthDate
) {
	public static UserInfo from(User user) {
		return new UserInfo(
			user.getLoginId(),
			user.getEmail(),
			user.getGender(),
			user.getBirthDate()
		);
	}
}
