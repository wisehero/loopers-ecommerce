package com.loopers.infrastructure.brand;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

	private final BrandJpaRepository brandJpaRepository;

	@Override
	public Brand save(Brand brand) {
		return brandJpaRepository.save(brand);
	}

	@Override
	public Optional<Brand> find(Long id) {
		return brandJpaRepository.findById(id);
	}
}
