package com.ledgercore.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="accounts")
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String kind;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal balance;
    protected Account() {}
    public Account(String name,String kind,BigDecimal balance){this.name=name;this.kind=kind;this.balance=balance;}
    public Long getId(){return id;} public String getName(){return name;} public String getKind(){return kind;}
    public BigDecimal getBalance(){return balance;}
    public void credit(BigDecimal amount){balance=balance.add(amount);} public void debit(BigDecimal amount){balance=balance.subtract(amount);}
}
