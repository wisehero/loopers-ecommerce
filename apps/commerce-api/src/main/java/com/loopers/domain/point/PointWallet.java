package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.LoginId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointWallet extends BaseEntity {

	@Embedded
	private LoginId loginId;

	@Embedded
	private Point balance;

	public static PointWallet create(LoginId loginId) {
		return new PointWallet(loginId, Point.of(0));
	}

	public void use(Point amount) {
		this.balance = this.balance.minus(amount);
	}

	public void deposit(Point amount) {
		if (amount.isZero()) {
			throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0일 수 없습니다.");
		}
		this.balance = this.balance.plus(amount);
	}
}
