package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PointTransactionHistory 엔티티 테스트")
class PointTransactionHistoryTest {

	@DisplayName("PointTransactionHistory의 팩토리 메서드는, ")
	@Nested
	class FactoryMethod {

		@DisplayName("useOf를 통해 사용 타입의 PointTransactionHistory를 생성할 수 있다.")
		@Test
		void userOf_createsUseTransactionHistory() {
			// given
			PointWallet mockPointWallet = mock(PointWallet.class);
			when(mockPointWallet.getId()).thenReturn(1L);

			Point amount = Point.of(500);

			// when
			PointTransactionHistory transaction = PointTransactionHistory.useOf(mockPointWallet, amount);

			// then
			assertAll(
				() -> assertThat(transaction.getPointWalletId()).isEqualTo(1L),
				() -> assertThat(transaction.getTransactionType()).isEqualTo(PointTransactionHistory.TransactionType.USE),
				() -> assertThat(transaction.getAmount()).isEqualTo(amount),
				() -> assertThat(transaction.getTransactionDateTime()).isNotNull()
			);
		}

		@DisplayName("depositOf를 통해 입금 타입의 PointTransactionHistory를 생성할 수 있다.")
		@Test
		void depositOf_createsDepositTransactionHistory() {
			// given
			PointWallet mockPointWallet = mock(PointWallet.class);
			when(mockPointWallet.getId()).thenReturn(1L);

			Point amount = Point.of(500);

			// when
			PointTransactionHistory transaction = PointTransactionHistory.depositOf(mockPointWallet, amount);

			// then
			assertAll(
				() -> assertThat(transaction.getPointWalletId()).isEqualTo(1L),
				() -> assertThat(transaction.getTransactionType()).isEqualTo(PointTransactionHistory.TransactionType.DEPOSIT),
				() -> assertThat(transaction.getAmount()).isEqualTo(amount),
				() -> assertThat(transaction.getTransactionDateTime()).isNotNull()
			);
		}
	}
}
