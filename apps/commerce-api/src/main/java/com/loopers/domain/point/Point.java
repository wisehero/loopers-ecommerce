package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Point {

	private long amount;

	private Point(long amount) {
		this.amount = amount;
	}

	public static Point of(long amount) {
		if (amount < 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0보다 작을 수 없습니다.");
		}
		return new Point(amount);
	}

	public Point plus(Point other) {
		return new Point(this.amount + other.amount);
	}

	public Point minus(Point other) {
		long newAmount = this.amount - other.amount;
		if (newAmount < 0) {
			throw new CoreException(ErrorType.BAD_REQUEST, "보유 포인트가 부족합니다.");
		}
		return new Point(newAmount);
	}

	public boolean isZero() {
		return this.amount == 0;
	}

	public long value() {
		return amount;
	}
}
