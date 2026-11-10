package com.ledgercore.api;
import com.ledgercore.dto.*;
import com.ledgercore.service.LedgerService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;
@RestController @RequestMapping("/api")
public class LedgerController {
    private final LedgerService ledger;
    public LedgerController(LedgerService ledger){this.ledger=ledger;}
    @GetMapping("/overview") public Map<String,Object> overview(){return ledger.overview();}
    @GetMapping("/accounts") public Object accounts(){return ledger.accounts();}
    @GetMapping("/transactions") public Object transactions(){return ledger.transactions();}
    @GetMapping("/budgets") public Object budgets(){return ledger.budgets();}
    @PostMapping("/transfer") public Map<String,String> transfer(@Valid @RequestBody TransferRequest r){ledger.transfer(r);return Map.of("message","Transfer completed");}
    @PostMapping("/expense") public Map<String,String> expense(@Valid @RequestBody EntryRequest r){ledger.expense(r);return Map.of("message","Expense recorded");}
    @PostMapping("/income") public Map<String,String> income(@Valid @RequestBody EntryRequest r){ledger.income(r);return Map.of("message","Income recorded");}
    @PutMapping("/budgets") public Map<String,String> budget(@Valid @RequestBody BudgetRequest r){ledger.upsertBudget(r);return Map.of("message","Budget saved");}
}
