package com.awin.recruitment.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Transaction {

    private Long transactionId;
    private LocalDate saleDate;
    private List<Product> products;

    public Transaction(Long transactionId, LocalDate saleDate, List<Product> products) {
        this.transactionId = transactionId;
        this.saleDate = saleDate;
        this.products = products;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {

        if (o != null && o.getClass() == getClass()) {
            Transaction that = (Transaction) o;
            return Objects.equals(getTransactionId(), that.getTransactionId()) &&
                    Objects.equals(getSaleDate(), that.getSaleDate()) &&
                    getProducts().equals(that.getProducts());
        }

        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTransactionId(), getSaleDate(), getProducts());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + getTransactionId() +
                ", saleDate=" + getSaleDate() +
                ", products=" + getProducts() +
                '}';
    }
}
