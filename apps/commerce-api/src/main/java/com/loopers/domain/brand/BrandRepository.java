package com.loopers.domain.brand;

import java.util.Optional;

public interface BrandRepository {

	Brand save(Brand brand);

	Optional<Brand> find(Long id);
}
