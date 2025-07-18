package com.loopers.interfaces.api.user;

import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserV1Dto {

	public record SignUpRequest(
		@NotBlank(message = "사용할 ID를 입력해주세요.")
		String userId,

		@NotBlank(message = "이메일을 입력해주세요.")
		@Email(message = "올바르지 않은 이메일 형식입니다.")
		String email,

		@NotBlank(message = "성별을 입력해주세요.")
		@Pattern(regexp = "^(MALE|FEMALE)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "성별은 MALE 또는 FEMALE이어야 합니다.")
		String gender,

		@NotBlank(message = "생년월일은 필수입니다.")
		@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식만 가능합니다.")
		String birthDate
	) {

		public UserCommand.Create toCommand() {
			return new UserCommand.Create(
				userId,
				email,
				gender,
				birthDate
			);
		}
	}

	public record UserResponse(
		String userId,
		String email,
		String gender,
		String birthDate
	) {

		public static UserV1Dto.UserResponse from(UserInfo userInfo) {
			return new UserV1Dto.UserResponse(
				userInfo.userId().value(),
				userInfo.email().getEmailAddress(),
				userInfo.gender().name(),
				userInfo.birthDate().toString()
			);
		}
	}
}
