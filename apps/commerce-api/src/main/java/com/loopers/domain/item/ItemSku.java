package com.loopers.domain.item;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemSku extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String color;

    private String size;

    private int stockQuantity;

    private BigDecimal additionalPrice;

    private boolean available;

    public ItemSku(Item item,
                   String color,
                   String size,
                   int stockQuantity,
                   BigDecimal additionalPrice) {
        this.item = Objects.requireNonNull(item);
        this.color = Objects.requireNonNull(color);
        this.size = Objects.requireNonNull(size);
        this.stockQuantity = stockQuantity;
        this.additionalPrice = Objects.requireNonNull(additionalPrice);
        this.available = true;
    }

    public void adjustStockQuantity(int delta) {
        int updated = this.stockQuantity + delta;
        if (updated < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고는 음수가 될 수 없습니다.");
        }
        this.stockQuantity = updated;
    }

    public void clearItem() {
        this.item = null;
    }

    public void enable() {
        this.available = true;
    }

    public void disable() {
        this.available = false;
    }

    public boolean isAvailable() {
        return available;
    }
}
