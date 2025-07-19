package com.loopers.infrastructure.point;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.point.PointWallet;
import com.loopers.domain.user.LoginId;

public interface PointWalletJpaRepository extends JpaRepository<PointWallet, Long> {

	Optional<PointWallet> findByLoginId(LoginId loginId);
}
