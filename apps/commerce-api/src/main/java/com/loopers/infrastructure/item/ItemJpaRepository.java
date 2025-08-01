package com.loopers.infrastructure.item;

import com.loopers.domain.item.Item;
import com.loopers.domain.item.ItemRepository;

import java.util.List;

public interface ItemJpaRepository extends ItemRepository {

    Item save(Item item);
}
