package com.awin.recruitment.data;

import java.math.BigDecimal;
import java.util.Objects;

public final class EnrichedTransaction extends Transaction{

    private BigDecimal totalCost;

    public EnrichedTransaction(Transaction transaction, BigDecimal totalCost) {
        super(transaction.getTransactionId(), transaction.getSaleDate(), transaction.getProducts());
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof EnrichedTransaction && super.equals(o)) {

            EnrichedTransaction that = (EnrichedTransaction) o;
            return Objects.equals(getTotalCost(), that.getTotalCost());

        }

        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), getTotalCost());
    }

    @Override
    public String toString() {
        return "EnrichedTransaction{" +
                "transactionId=" + super.getTransactionId() +
                ", saleDate=" + super.getSaleDate() +
                ", products=" + super.getProducts() +
                "totalCost=" + getTotalCost() +
                '}';
    }
}
