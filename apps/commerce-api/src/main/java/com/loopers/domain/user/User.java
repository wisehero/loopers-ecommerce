package com.loopers.domain.user;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class User extends BaseEntity {

	@Embedded
	private UserId userId;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Embedded
	private Email email;

	private LocalDate birthDate;

	public static User create(UserCommand.Create command) {
		UserId userId = UserId.of(command.userId());
		Email email = Email.of(command.email());
		Gender gender = Gender.fromInput(command.gender());
		LocalDate birthDate = parseBirthDate(command.birthDate());

		return new User(
			userId,
			gender,
			email,
			birthDate
		);
	}

	private static LocalDate parseBirthDate(String birthDateString) {
		if (birthDateString == null) {
			throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어 있을 수 없습니다.");
		}

		try {
			return LocalDate.parse(birthDateString);
		} catch (DateTimeParseException e) {
			throw new CoreException(ErrorType.BAD_REQUEST, "생년월일의 형식이 올바르지 않습니다. (yyyy-MM-dd)");
		}
	}
}
