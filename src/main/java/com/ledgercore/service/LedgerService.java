package com.ledgercore.service;
import com.ledgercore.dto.*;
import com.ledgercore.model.*;
import com.ledgercore.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class LedgerService {
    private final AccountRepository accounts; private final EntryRepository entries; private final BudgetRepository budgets;
    public LedgerService(AccountRepository a,EntryRepository e,BudgetRepository b){accounts=a;entries=e;budgets=b;}
    public List<Account> accounts(){return accounts.findAll();}
    public List<LedgerEntry> transactions(){return entries.findAllByOrderByOccurredAtDescIdDesc();}
    public List<Budget> budgets(){return budgets.findAll();}
    private Account account(Long id){return accounts.findById(id).orElseThrow(()->new IllegalArgumentException("Account not found"));}
    private BigDecimal amount(BigDecimal value){if(value==null||value.signum()<=0||value.scale()>2||value.precision()>14)throw new IllegalArgumentException("Enter a valid positive amount with at most two decimals");return value;}
    private String label(String s){if(s==null||s.isBlank()||s.length()>80)throw new IllegalArgumentException("Provide a name with 1–80 characters");return s.trim();}
    @Transactional
    public void transfer(TransferRequest r){
        BigDecimal x=amount(r.amount()); if(r.fromAccountId().equals(r.toAccountId()))throw new IllegalArgumentException("Choose two different accounts");
        Account from=account(r.fromAccountId()),to=account(r.toAccountId());
        if(from.getBalance().compareTo(x)<0)throw new IllegalArgumentException("Insufficient funds in source account");
        from.debit(x);to.credit(x);accounts.saveAll(List.of(from,to));
        String ref=UUID.randomUUID().toString();LocalDateTime now=LocalDateTime.now();
        entries.saveAll(List.of(new LedgerEntry(from,"Transfer to "+to.getName(),"Transfer","TRANSFER_OUT",x,now,ref),new LedgerEntry(to,"Transfer from "+from.getName(),"Transfer","TRANSFER_IN",x,now,ref)));
    }
    @Transactional
    public void expense(EntryRequest r){
        Account a=account(r.accountId());BigDecimal x=amount(r.amount());if(a.getBalance().compareTo(x)<0)throw new IllegalArgumentException("Insufficient account balance");
        a.debit(x);accounts.save(a);entries.save(new LedgerEntry(a,label(r.merchant()),label(r.category()),"EXPENSE",x,LocalDateTime.now(),null));
    }
    @Transactional
    public void income(EntryRequest r){
        Account a=account(r.accountId());BigDecimal x=amount(r.amount());a.credit(x);accounts.save(a);
        entries.save(new LedgerEntry(a,label(r.merchant()),label(r.category()),"INCOME",x,LocalDateTime.now(),null));
    }
    @Transactional
    public void upsertBudget(BudgetRequest r){
        BigDecimal x=amount(r.monthlyLimit());String category=label(r.category());
        Budget b=budgets.findByCategory(category).orElseGet(()->new Budget(category,x));b.setMonthlyLimit(x);budgets.save(b);
    }
    public Map<String,Object> overview(){
        List<LedgerEntry> all=transactions();LocalDate today=LocalDate.now();YearMonth month=YearMonth.from(today);
        List<LedgerEntry> current=all.stream().filter(e->YearMonth.from(e.getOccurredAt()).equals(month)).toList();
        BigDecimal total=accounts().stream().map(Account::getBalance).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal spent=current.stream().filter(e->e.getType().equals("EXPENSE")).map(LedgerEntry::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal earned=current.stream().filter(e->e.getType().equals("INCOME")).map(LedgerEntry::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        Map<String,BigDecimal> categorySpend=current.stream().filter(e->e.getType().equals("EXPENSE")).collect(Collectors.groupingBy(LedgerEntry::getCategory,Collectors.reducing(BigDecimal.ZERO,LedgerEntry::getAmount,BigDecimal::add)));
        List<Map<String,Object>> budgetViews=budgets().stream().map(b->{Map<String,Object> m=new LinkedHashMap<>();m.put("category",b.getCategory());m.put("limit",b.getMonthlyLimit());m.put("spent",categorySpend.getOrDefault(b.getCategory(),BigDecimal.ZERO));return m;}).toList();
        List<Map<String,Object>> weekly=new ArrayList<>();
        for(int i=6;i>=0;i--){LocalDate date=today.minusDays(i);BigDecimal inc=BigDecimal.ZERO,exp=BigDecimal.ZERO;
            for(LedgerEntry e:all){if(e.getOccurredAt().toLocalDate().equals(date)){if(e.getType().equals("INCOME"))inc=inc.add(e.getAmount());else if(e.getType().equals("EXPENSE"))exp=exp.add(e.getAmount());}}
            weekly.add(Map.of("date",date.toString(),"income",inc,"expense",exp));}
        Map<String,Object> m=new LinkedHashMap<>();m.put("totalBalance",total);m.put("monthlyIncome",earned);m.put("monthlyExpenses",spent);m.put("monthlyNet",earned.subtract(spent));m.put("accountCount",accounts.count());m.put("transactionCount",all.size());m.put("categorySpend",categorySpend);m.put("budgets",budgetViews);m.put("weekly",weekly);m.put("month",month.toString());return m;
    }
}
