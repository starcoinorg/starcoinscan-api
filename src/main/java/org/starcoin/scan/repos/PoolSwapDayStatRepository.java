package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.starcoin.scan.repos.entity.PoolSwapDayStat;
import org.starcoin.scan.repos.entity.PoolSwapDayStatId;

import java.util.List;

public interface PoolSwapDayStatRepository extends JpaRepository<PoolSwapDayStat, PoolSwapDayStatId> {

    @Query(value="select * from :network.pool_swap_day_stat limit :count offset :offset",nativeQuery = true)
    List<PoolSwapDayStat> findAll(@Param("offset")int offset,
                                  @Param("count")int count,
                                  @Param("network") String network);

    @Query(value="select * from :network.pool_swap_day_stat where (token_a=:token_x_name and token_b=:token_y_name) or (token_a=:token_y_name and token_b=:token_x_name) order by volume limit 1",nativeQuery = true)
    PoolSwapDayStat find(@Param("network") String network,
                         @Param("token_x_name")String tokenXName,@Param("token_y_name")String tokenYName);

    @Query(value="select * from :network.pool_swap_day_stat where token_name=:token_name order by volume limit :count offset :offset",nativeQuery = true)
    List<PoolSwapDayStat> findAll(@Param("offset")int offset,
                            @Param("count")int count,
                            @Param("network") String network,
                            @Param("token_name") String tokenName);

}