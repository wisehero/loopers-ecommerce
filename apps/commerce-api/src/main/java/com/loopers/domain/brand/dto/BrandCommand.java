package com.loopers.domain.brand.dto;

public record BrandCommand(
) {

	public record Create(
		String brandName,
		String description
	) {

	}
}
