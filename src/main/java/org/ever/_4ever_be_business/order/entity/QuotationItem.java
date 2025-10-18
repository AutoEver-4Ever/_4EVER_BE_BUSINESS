package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.order.enums.Unit;

import java.math.BigDecimal;

@Entity
@Table(name="quotation_item")
@NoArgsConstructor
@Getter
public class QuotationItem extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="quotation_id")
    private Quotation quotationId;

    @Column(nullable = false, name="product_id")
    private Long productId;

    @Column(nullable = false, name="count")
    private Long count;

    @Column(nullable = false, name="unit")
    private Unit unit;

    @Column(nullable = false, name="price")
    private BigDecimal price;

    public QuotationItem(Quotation quotationId, Long productId, Long count, Unit unit, BigDecimal price) {
        this.quotationId = quotationId;
        this.productId = productId;
        this.count = count;
        this.unit = unit;
        this.price = price;
    }
}
