package com.loopers.domain.point;

import java.time.ZonedDateTime;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_transaction_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransactionHistory extends BaseEntity {

	private Long pointWalletId;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	private Point amount;

	private ZonedDateTime transactionDateTime;

	public enum TransactionType {
		USE,
		DEPOSIT
	}

	private PointTransactionHistory(
		Long pointWalletId,
		TransactionType transactionType,
		Point amount
	) {
		this.pointWalletId = pointWalletId;
		this.transactionType = transactionType;
		this.amount = amount;
		this.transactionDateTime = ZonedDateTime.now();
	}

	public static PointTransactionHistory useOf(PointWallet wallet, Point amount) {
		return new PointTransactionHistory(
			wallet.getId(),
			TransactionType.USE,
			amount
		);
	}

	public static PointTransactionHistory depositOf(PointWallet wallet, Point amount) {
		return new PointTransactionHistory(
			wallet.getId(),
			TransactionType.DEPOSIT,
			amount
		);
	}
}
