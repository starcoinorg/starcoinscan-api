package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.scan.repos.entity.TokenSwapDayStat;
import org.starcoin.scan.repos.entity.TokenSwapDayStatId;

public interface TokenSwapDayStatRepository extends JpaRepository<TokenSwapDayStat, TokenSwapDayStatId> {
}