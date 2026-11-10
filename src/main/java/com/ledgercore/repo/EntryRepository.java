package com.ledgercore.repo;
import com.ledgercore.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EntryRepository extends JpaRepository<LedgerEntry,Long> {
    List<LedgerEntry> findAllByOrderByOccurredAtDescIdDesc();
}
