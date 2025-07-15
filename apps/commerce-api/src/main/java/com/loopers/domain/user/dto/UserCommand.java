package com.loopers.domain.user.dto;

public class UserCommand {

	public record Create(
		String userId,
		String email,
		String gender,
		String birthDate
	) {

	}
}
