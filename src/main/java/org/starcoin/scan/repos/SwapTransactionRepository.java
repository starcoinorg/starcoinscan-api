package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.scan.repos.entity.SwapTransaction;

public interface SwapTransactionRepository extends JpaRepository<SwapTransaction, String> {
}