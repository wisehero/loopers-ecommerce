package com.loopers.domain.user.dto;

import java.time.LocalDate;

import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserId;

public record UserInfo(
	UserId userId,
	Email email,
	Gender gender,
	LocalDate birthDate
) {
	public static UserInfo from(User user) {
		return new UserInfo(
			user.getUserId(),
			user.getEmail(),
			user.getGender(),
			user.getBirthDate()
		);
	}
}
