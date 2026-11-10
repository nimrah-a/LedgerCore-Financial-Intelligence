package com.ledgercore.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="budgets",uniqueConstraints=@UniqueConstraint(columnNames="category"))
public class Budget {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String category;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal monthlyLimit;
    protected Budget() {}
    public Budget(String category,BigDecimal monthlyLimit){this.category=category;this.monthlyLimit=monthlyLimit;}
    public Long getId(){return id;} public String getCategory(){return category;}
    public BigDecimal getMonthlyLimit(){return monthlyLimit;}
    public void setMonthlyLimit(BigDecimal limit){this.monthlyLimit=limit;}
}
