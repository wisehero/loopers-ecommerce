package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.loopers.domain.user.LoginId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class PointWalletTest {

	@DisplayName("PointWallet을 생성할 때, ")
	@Nested
	class Create {

		@DisplayName("create 메서드는 초기 잔액이 0인 지갑을 생성한다.")
		void create_returnsWalletWithZeroBalance() {
			// given
			LoginId mockLoginId = mock(LoginId.class);

			// when
			PointWallet wallet = PointWallet.create(mockLoginId);

			// then
			assertAll(
				() -> assertThat(wallet.getLoginId()).isEqualTo(mockLoginId),
				() -> assertThat(wallet.getBalance().value()).isEqualTo(0L)
			);
		}
	}

	@DisplayName("포인트를 충전할 때, ")
	@Nested
	class Deposit {

		@DisplayName("0보다 큰 금액을 충전하면 충전한만큼 잔액이 증가한다.")
		@Test
		void deposit_increasesBalance_whenAmountIsPositive() {
			// given
			PointWallet wallet = PointWallet.create(mock(LoginId.class));
			Point amountToDeposit = Point.of(1000L);

			// when
			wallet.deposit(amountToDeposit);

			// then
			assertThat(wallet.getBalance().value()).isEqualTo(1000L);
		}

		@DisplayName("0 포인트를 충전하려고 하면, BAD_REQUEST 예외가 발생한다.")
		@Test
		void deposit_throwsException_whenAmountIsZero() {
			// given
			PointWallet wallet = PointWallet.create(mock(LoginId.class));
			Point amountToDeposit = Point.of(0);

			// when & then
			assertThatThrownBy(() -> wallet.deposit(amountToDeposit))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("충전 금액은 0일 수 없습니다.");
		}
	}

	@DisplayName("포인트를 사용할 때, ")
	@Nested
	class Use {

		@DisplayName("잔액이 충분하면, 잔액이 사용한만큼 감소한다.")
		@Test
		void use_decreasesBalance_whenSufficientBalance() {
			// given
			PointWallet wallet = PointWallet.create(mock(LoginId.class));
			wallet.deposit(Point.of(1000L));
			Point amountToUse = Point.of(300L);

			// when
			wallet.use(amountToUse);

			// then
			assertThat(wallet.getBalance().value()).isEqualTo(700L);
		}

		@DisplayName("잔액이 부족하면, 예외가 발생한다.")
		@Test
		void use_throwsException_whenInsufficientBalance() {
			// given
			PointWallet wallet = PointWallet.create(mock(LoginId.class));
			wallet.deposit(Point.of(100L));
			Point amountToUse = Point.of(200L);

			// whne & then
			assertThatThrownBy(() -> wallet.use(amountToUse))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("보유 포인트가 부족합니다.");
		}
	}
}
