package com.loopers.domain.brand;

import org.springframework.stereotype.Service;

import com.loopers.domain.brand.dto.BrandCommand;
import com.loopers.domain.brand.dto.BrandResult;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

	private final BrandRepository brandRepository;

	public Brand create(BrandCommand.Create command) {
		Brand brand = Brand.create(command.brandName(), command.description());

		return brandRepository.save(brand);
	}

	public BrandResult.BrandInfo findBrand(Long brandId) {
		Brand foundBrand = brandRepository.find(brandId)
			.orElseThrow(
				() -> new CoreException(ErrorType.NOT_FOUND, "조회 요청한 브랜드가 없습니다.")
			);

		return BrandResult.BrandInfo.from(foundBrand);
	}
}
