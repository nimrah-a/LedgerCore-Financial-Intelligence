package com.ledgercore.repo;
import com.ledgercore.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BudgetRepository extends JpaRepository<Budget,Long> {Optional<Budget> findByCategory(String category);}
