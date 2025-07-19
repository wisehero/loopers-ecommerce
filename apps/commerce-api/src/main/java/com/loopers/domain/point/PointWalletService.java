package com.loopers.domain.point;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.domain.user.LoginId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointWalletService {

	private final PointWalletRepository pointWalletRepository;

	public PointWallet getWalletByLoginId(LoginId loginId) {
		return pointWalletRepository.findByLoginId(loginId)
			.orElseGet(() -> pointWalletRepository.save(PointWallet.create(loginId)));
	}

	@Transactional
	public PointWallet deposit(LoginId loginId, Point amountToDeposit) {
		PointWallet wallet = pointWalletRepository.findByLoginId(loginId)
			.orElseGet(() -> pointWalletRepository.save(PointWallet.create(loginId)));

		wallet.deposit(amountToDeposit);
		PointTransactionHistory transactionHistory = PointTransactionHistory.depositOf(wallet, amountToDeposit);

		return pointWalletRepository.saveWithHistory(wallet, transactionHistory);
	}

	public PointWallet use(LoginId loginId, Point amountToDeposit) {
		PointWallet wallet = pointWalletRepository.findByLoginId(loginId)
			.orElseThrow(() -> new CoreException(ErrorType.CONFLICT, "지갑을 소유한 사용자가 존재하지 않습니다."));

		wallet.use(amountToDeposit);
		PointTransactionHistory transactionHistory = PointTransactionHistory.useOf(wallet, amountToDeposit);

		return pointWalletRepository.saveWithHistory(wallet, transactionHistory);
	}
}
