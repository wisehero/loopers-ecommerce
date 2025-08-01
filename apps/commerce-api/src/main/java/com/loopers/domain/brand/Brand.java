package com.loopers.domain.brand;

import org.springframework.util.StringUtils;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Brand extends BaseEntity {

	private String brandName;

	private String description;

	private boolean isActive;

	public static Brand create(String brandName, String description) {
		if (!StringUtils.hasText(brandName)) {
			throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 이름은 비어있을 수 없습니다.");
		}

		if (!StringUtils.hasText(description)) {
			throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 설명은 비어있을 수 없습니다.");
		}

		Brand brand = new Brand();
		brand.brandName = brandName;
		brand.description = description;

		return brand;
	}

	public void activate() {
		this.isActive = true;
	}

	public void deactivate() {
		this.isActive = false;
	}
}
