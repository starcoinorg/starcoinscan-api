package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.starcoin.scan.repos.entity.TokenSwapDayStat;
import org.starcoin.scan.repos.entity.TokenSwapDayStatId;

import java.util.List;

public interface TokenSwapDayStatRepository extends JpaRepository<TokenSwapDayStat, TokenSwapDayStatId> {

    @Query(value="select * from :network.token_swap_day_stat limit :count offset :offset",nativeQuery = true)
    List<TokenSwapDayStat> findAll(@Param("offset")int offset,
                                   @Param("count")int count,
                                   @Param("network") String network);

    @Query(value="select * from :network.token_swap_day_stat where token_name=:token_name order by ts limit 1",nativeQuery = true)
    TokenSwapDayStat find(@Param("network") String network,@Param("token_name")String tokenName);

}