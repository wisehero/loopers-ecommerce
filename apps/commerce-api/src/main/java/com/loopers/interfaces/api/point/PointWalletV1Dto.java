package com.loopers.interfaces.api.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointWalletCommand;
import com.loopers.domain.point.PointWalletResult;
import com.loopers.domain.user.LoginId;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PointWalletV1Dto {

	public record DepositRequest(
		@NotNull(message = "충전할 포인트를 입력해주세요.")
		@Positive(message = "충전할 포인트는 0보다 커야합니다.")
		Long amount
	) {

		public PointWalletCommand.Deposit toCommand(String loginId) {
			LoginId loginIdVO = LoginId.of(loginId);
			Point amountVO = Point.of(this.amount);
			return new PointWalletCommand.Deposit(loginIdVO, amountVO);
		}
	}

	public record DepositResponse(
		String loginId,
		long balance
	) {

		public static DepositResponse from(PointWalletResult.BalanceInfo balanceInfo) {
			return new DepositResponse(
				balanceInfo.loginId(),
				balanceInfo.balance()
			);
		}
	}

	public record BalanceResponse(
		String loginId,
		long balance
	) {
		public static BalanceResponse from(PointWalletResult.BalanceInfo balanceInfo) {
			return new BalanceResponse(
				balanceInfo.loginId(),
				balanceInfo.balance()
			);
		}
	}
}
