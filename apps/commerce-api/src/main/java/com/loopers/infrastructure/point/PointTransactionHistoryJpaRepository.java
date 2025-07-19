package com.loopers.infrastructure.point;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.point.PointTransactionHistory;

public interface PointTransactionHistoryJpaRepository extends JpaRepository<PointTransactionHistory, Long> {
}
