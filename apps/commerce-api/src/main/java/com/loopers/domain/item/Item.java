package com.loopers.domain.item;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Item extends BaseEntity {

    private Long brandId;

    private String name;

    private BigDecimal basePrice;

    public Item create(Long brandId, String name, BigDecimal basePrice) {
        Item item = new Item();
        this.brandId = brandId;
        this.name = name;
        this.basePrice = basePrice;
        return item;
    }

    @OneToMany(
            mappedBy = "item",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ItemSku> skus = new ArrayList<>();

    public List<ItemSku> getSkus() {
        return Collections.unmodifiableList(skus);
    }

    public void addSku(String color,
                       String size,
                       int stockQuantity,
                       BigDecimal additionalPrice) {
        boolean exists = skus.stream()
                .anyMatch(s -> s.getColor().equals(color) && s.getSize().equals(color));
        if (exists) {
            throw new CoreException(ErrorType.BAD_REQUEST,
                    "이미 존재하는 옵션입니다. color = " + color + ", size = " + size);
        }
    }

    public void removeSku(ItemSku sku) {
        skus.remove(sku);
        sku.clearItem();
    }
}
