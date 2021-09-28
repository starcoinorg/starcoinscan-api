package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.scan.repos.entity.PoolSwapDayStat;
import org.starcoin.scan.repos.entity.PoolSwapDayStatId;

public interface PoolSwapDayStatRepository extends JpaRepository<PoolSwapDayStat, PoolSwapDayStatId> {
}