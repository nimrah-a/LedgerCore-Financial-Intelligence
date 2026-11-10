package com.ledgercore.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="ledger_entries",indexes={@Index(name="idx_entry_time",columnList="occurredAt")})
public class LedgerEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) private Account account;
    @Column(nullable=false) private String merchant;
    @Column(nullable=false) private String category;
    @Column(nullable=false) private String type;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(nullable=false) private LocalDateTime occurredAt;
    private String referenceId;
    protected LedgerEntry() {}
    public LedgerEntry(Account account,String merchant,String category,String type,BigDecimal amount,LocalDateTime at,String ref){
        this.account=account;this.merchant=merchant;this.category=category;this.type=type;this.amount=amount;this.occurredAt=at;this.referenceId=ref;
    }
    public Long getId(){return id;} public Account getAccount(){return account;}
    public String getMerchant(){return merchant;} public String getCategory(){return category;}
    public String getType(){return type;} public BigDecimal getAmount(){return amount;}
    public LocalDateTime getOccurredAt(){return occurredAt;} public String getReferenceId(){return referenceId;}
}
