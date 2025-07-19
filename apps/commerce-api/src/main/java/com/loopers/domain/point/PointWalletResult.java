package com.loopers.domain.point;

public class PointWalletResult {

	public record BalanceInfo(
		String loginId,
		long balance
	) {

		public static PointWalletResult.BalanceInfo of(PointWallet wallet) {
			return new PointWalletResult.BalanceInfo(
				wallet.getLoginId().value(),
				wallet.getBalance().value()
			);
		}
	}
}
