package com.loopers.domain.point;

import com.loopers.domain.user.LoginId;

public class PointWalletCommand {

	public record Deposit(
		LoginId loginId,
		Point amount
	) {

	}

	public record Use(
		LoginId loginId,
		Point amount
	) {
	}
}
