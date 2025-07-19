package com.loopers.infrastructure.point;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.domain.point.PointTransactionHistory;
import com.loopers.domain.point.PointWallet;
import com.loopers.domain.point.PointWalletRepository;
import com.loopers.domain.user.LoginId;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PointWalletRepositoryImpl implements PointWalletRepository {

	private final PointWalletJpaRepository pointWalletJpaRepository;
	private final PointTransactionHistoryJpaRepository pointTransactionHistoryJpaRepository;

	@Override
	public PointWallet save(PointWallet pointWallet) {
		return pointWalletJpaRepository.save(pointWallet);
	}

	public PointWallet saveWithHistory(PointWallet wallet, PointTransactionHistory transaction) {
		PointWallet savedPointWallet = pointWalletJpaRepository.save(wallet);
		pointTransactionHistoryJpaRepository.save(transaction);

		return savedPointWallet;
	}

	@Override
	public Optional<PointWallet> findByLoginId(LoginId loginId) {
		return pointWalletJpaRepository.findByLoginId(loginId);
	}
}
