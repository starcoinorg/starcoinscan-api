package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.starcoin.scan.repos.entity.SwapDayStat;

import java.time.LocalTime;
import java.util.List;

public interface SwapDayStatRepository extends JpaRepository<SwapDayStat, LocalTime> {
    @Query(value="select * from :network.swap_day_stat limit :count offset :offset",nativeQuery = true)
    List<SwapDayStat> findAll(@Param("offset")int offset,
                                   @Param("count")int count,
                                   @Param("network") String network);

}