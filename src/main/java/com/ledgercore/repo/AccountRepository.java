package com.ledgercore.repo;
import com.ledgercore.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account,Long> {}
