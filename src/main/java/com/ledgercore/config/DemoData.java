package com.ledgercore.config;
import com.ledgercore.model.*;
import com.ledgercore.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
@Configuration
public class DemoData {
    private BigDecimal usd(String s){return new BigDecimal(s);}
    @Bean CommandLineRunner seed(AccountRepository accounts,EntryRepository entries,BudgetRepository budgets){return args->{
        if(accounts.count()>0)return;
        Account checking=accounts.save(new Account("Everyday Checking","CHECKING",usd("16250.80")));
        Account savings=accounts.save(new Account("High-Yield Savings","SAVINGS",usd("24820.00")));
        Account reserve=accounts.save(new Account("Travel Reserve","SAVINGS",usd("3842.20")));
        for(String[] b:new String[][]{{"Food & Drink","850"},{"Shopping","700"},{"Transport","450"},{"Bills","1800"},{"Entertainment","300"},{"Health","400"}})
            budgets.save(new Budget(b[0],usd(b[1])));
        LocalDate today=LocalDate.now();List<LedgerEntry> generated=new ArrayList<>();
        String[][] items={{"Blue Bottle Coffee","Food & Drink","7.80"},{"Whole Foods Market","Food & Drink","126.35"},{"Spotify Premium","Entertainment","11.99"},{"CTA Transit Pass","Transport","75.00"},{"Adobe Creative Cloud","Bills","54.99"},{"Muji Store","Shopping","68.00"},{"Sweetgreen","Food & Drink","18.45"},{"Uber Trip","Transport","22.80"},{"Electric Utility","Bills","132.40"},{"Bookshop","Shopping","39.90"},{"Cinema Tickets","Entertainment","28.00"},{"Pharmacy","Health","24.75"},{"Internet Provider","Bills","84.00"},{"Iced Latte","Food & Drink","6.50"},{"Gym Membership","Health","42.00"}};
        for(int i=0;i<items.length;i++){
            String[] row=items[i];LocalDate date=today.minusDays(i%7);generated.add(new LedgerEntry(checking,row[0],row[1],"EXPENSE",usd(row[2]),date.atTime(10+i%10,15),null));
        }
        generated.add(new LedgerEntry(checking,"Demo Salary Deposit","Income","INCOME",usd("4250.00"),today.minusDays(2).atTime(9,0),null));
        generated.add(new LedgerEntry(checking,"Freelance Payment","Income","INCOME",usd("780.00"),today.minusDays(6).atTime(11,30),null));
        entries.saveAll(generated);
        // Seeded balances are intentionally opening demo balances, not reconstructed from the synthetic activity log.
    };}
}
