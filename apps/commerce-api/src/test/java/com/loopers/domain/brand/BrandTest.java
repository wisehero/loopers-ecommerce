package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class BrandTest {

	@Nested
	@DisplayName("브랜드 객체를 생성할 때, ")
	class Create {

		@DisplayName("브랜드 이름, 브랜드 설명이 주어지면 브랜드가 생성된다. 초기에는 비활성화 상태다.")
		@Test
		void createBrand_whenInputIsValid() {
			// given
			String brandName = "나이키";
			String description = "Just Do It";

			// when
			Brand brand = Brand.create(brandName, description);

			// then
			assertAll(
				() -> assertThat(brand.getBrandName()).isEqualTo(brandName),
				() -> assertThat(brand.getDescription()).isEqualTo(description),
				() -> assertThat(brand.isActive()).isFalse()
			);
		}

		@DisplayName("브랜드 이름이 유효하지 않으면, BAD_REQUEST 예외가 발생한다.")
		@ParameterizedTest(name = "입력값: [{arguments}]")
		@NullAndEmptySource
		@ValueSource(strings = {" "})
		void throwsException_whenBrandNameIsInvalid(String invalidName) {
			// given
			String description = "유효한 설명";

			// when & then
			assertThatThrownBy(() -> Brand.create(invalidName, description))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("브랜드 이름은 비어있을 수 없습니다.");
		}

		@DisplayName("브랜드 설명(description)이 유효하지 않으면, BAD_REQUEST 예외가 발생한다.")
		@ParameterizedTest(name = "입력값: [{arguments}]")
		@NullAndEmptySource
		@ValueSource(strings = {" ", "   "})
		void throwsException_whenDescriptionIsInvalid(String invalidDescription) {
			// given
			String brandName = "유효한 이름";

			// when & then
			assertThatThrownBy(() -> Brand.create(brandName, invalidDescription))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ErrorType.BAD_REQUEST)
				.hasMessage("브랜드 설명은 비어있을 수 없습니다.");
		}
	}

	@DisplayName("브랜드는 활성화할 수 있다.")
	@Test
	void activate_brand() {
		// given
		Brand brand = Brand.create("나이키", "Just Do It");

		// when
		brand.activate();

		// then
		assertThat(brand.isActive()).isTrue();
	}

	@DisplayName("브랜드는 비활성화 할 수 있다.")
	@Test
	void deactivate_brand() {
		// given
		Brand brand = Brand.create("나이키", "Just Do It");
		brand.activate();

		// when
		brand.deactivate();

		// then
		assertThat(brand.isActive()).isFalse();
	}
}
