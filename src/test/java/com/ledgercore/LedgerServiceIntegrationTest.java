package com.ledgercore;
import com.ledgercore.dto.*;
import com.ledgercore.model.Account;
import com.ledgercore.repo.AccountRepository;
import com.ledgercore.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:ledgercoretest;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop"})
@Transactional
class LedgerServiceIntegrationTest {
    @Autowired LedgerService service;
    @Autowired AccountRepository accounts;
    @Test void transferMovesMoneyWithoutChangingTotal(){
        var a=service.accounts();var first=a.get(0);var second=a.get(1);
        var originalTotal=(BigDecimal) service.overview().get("totalBalance");
        BigDecimal from=first.getBalance(),to=second.getBalance();
        service.transfer(new TransferRequest(first.getId(),second.getId(),new BigDecimal("25.00")));
        assertEquals(0,accounts.findById(first.getId()).orElseThrow().getBalance().compareTo(from.subtract(new BigDecimal("25.00"))));
        assertEquals(0,accounts.findById(second.getId()).orElseThrow().getBalance().compareTo(to.add(new BigDecimal("25.00"))));
        assertEquals(0,originalTotal.compareTo((BigDecimal) service.overview().get("totalBalance")));
    }
    @Test void preventsOverdraftAndSameAccountTransfer(){
        Account a=service.accounts().get(0);
        assertThrows(IllegalArgumentException.class,()->service.transfer(new TransferRequest(a.getId(),a.getId(),new BigDecimal("5"))));
        assertThrows(IllegalArgumentException.class,()->service.expense(new EntryRequest(a.getId(),"Shop","Shopping",new BigDecimal("99999999"))));
    }
    @Test void expenseAndIncomeUpdateBalance(){
        Account a=service.accounts().get(0);BigDecimal before=a.getBalance();
        service.expense(new EntryRequest(a.getId(),"Test Store","Shopping",new BigDecimal("20")));
        service.income(new EntryRequest(a.getId(),"Test Pay","Income",new BigDecimal("45")));
        assertEquals(0,accounts.findById(a.getId()).orElseThrow().getBalance().compareTo(before.add(new BigDecimal("25"))));
    }
    @Test void budgetsAreEditable(){service.upsertBudget(new BudgetRequest("Shopping",new BigDecimal("1234")));assertTrue(service.budgets().stream().anyMatch(b->b.getCategory().equals("Shopping")&&b.getMonthlyLimit().compareTo(new BigDecimal("1234"))==0));}
}
