package com.loopers.domain.point;

import java.util.Optional;

import com.loopers.domain.user.LoginId;

public interface PointWalletRepository {

	PointWallet save(PointWallet pointWallet);

	PointWallet saveWithHistory(PointWallet wallet, PointTransactionHistory transaction);

	Optional<PointWallet> findByLoginId(LoginId loginId);
}
