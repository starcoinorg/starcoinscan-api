package org.starcoin.scan.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.starcoin.scan.repos.entity.SwapTransaction;

import java.util.List;

public interface SwapTransactionRepository extends JpaRepository<SwapTransaction, String> {

    @Query(value="select * from :network.swap_transaction  where swap_type=:txn_type and swap_seq<:swap_seq limit :count",nativeQuery = true)
    public List<SwapTransaction> findByType(@Param("network")String network,
            @Param("txn_type")int swapType,
            @Param("swap_seq")int swapSeq,
            @Param("count") int count);

    @Query(value="select * from :network.swap_transaction  where swap_seq<:swap_seq limit :count",nativeQuery = true)
    public List<SwapTransaction> find(@Param("network")String network,
            @Param("swap_seq")int swapSeq, @Param("count") int count);


}