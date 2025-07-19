package com.loopers.application.point;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.domain.point.PointWallet;
import com.loopers.domain.point.PointWalletCommand;
import com.loopers.domain.point.PointWalletResult;
import com.loopers.domain.point.PointWalletService;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointWalletFacade {

	private final PointWalletService pointWalletService;
	private final UserService userService;

	@Transactional
	public PointWalletResult.BalanceInfo deposit(PointWalletCommand.Deposit command) {
		User foundUser = userService.findByUserId(command.loginId())
			.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "지갑을 소유한 사용자가 존재하지 않습니다."));

		PointWallet updatedWallet = pointWalletService.deposit(foundUser.getLoginId(), command.amount());

		return PointWalletResult.BalanceInfo.of(updatedWallet);
	}

	public PointWalletResult.BalanceInfo getBalance(String loginId) {
		LoginId loginIdVO = LoginId.of(loginId);
		userService.findByUserId(loginIdVO)
			.orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "지갑을 소유한 사용자가 존재하지 않습니다."));

		PointWallet wallet = pointWalletService.getWalletByLoginId(loginIdVO);
		return PointWalletResult.BalanceInfo.of(wallet);
	}

	public PointWalletResult.BalanceInfo use(PointWalletCommand.Use command) {

		PointWallet updatedWallet = pointWalletService.use(command.loginId(), command.amount());

		return PointWalletResult.BalanceInfo.of(updatedWallet);
	}
}
