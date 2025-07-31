package com.loopers.domain.brand.dto;

import com.loopers.domain.brand.Brand;

public record BrandResult() {

	public record BrandInfo(
		Long brandId,
		String brandName,
		String description
	) {

		public static BrandInfo from(Brand brand) {
			return new BrandInfo(
				brand.getId(),
				brand.getBrandName(),
				brand.getDescription()
			);
		}

	}
}
