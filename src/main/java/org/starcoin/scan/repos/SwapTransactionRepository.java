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


    @Query(value="select * from :network.swap_transaction  where swap_seq<:swap_seq and (token_a=:token_name or token_b=:token_name) limit :count",nativeQuery = true)
    public List<SwapTransaction> findByTokenName(@Param("network")String network,
                                                 @Param("token_name")String tokenName,
                                      @Param("swap_seq")int swapSeq, @Param("count") int count);

    @Query(value="select * from :network.swap_transaction  where swap_type=:txn_type and swap_seq<:swap_seq and (token_a=:token_name or token_b=:token_name) limit :count",nativeQuery = true)
    public List<SwapTransaction> findByTypeAndTokenName(@Param("network")String network,
                                                        @Param("token_name")String tokenName,
                                            @Param("txn_type")int swapType,
                                            @Param("swap_seq")int swapSeq,
                                            @Param("count") int count);

    @Query(value="select * from :network.swap_transaction  where swap_seq<:swap_seq and ((token_a=:token_x_name and token_b=:token_y_name) or (token_a=:token_y_name and token_b=:token_x_name)) limit :count",nativeQuery = true)
    public List<SwapTransaction> findByTokenPair(@Param("network")String network,
                                                 @Param("token_x_name")String tokenXName,
                                                 @Param("token_y_name")String tokenYName,
                                                 @Param("swap_seq")int swapSeq, @Param("count") int count);

    @Query(value="select * from :network.swap_transaction  where swap_type=:txn_type and swap_seq<:swap_seq and ((token_a=:token_x_name and token_b=:token_y_name) or (token_a=:token_y_name and token_b=:token_x_name)) limit :count",nativeQuery = true)
    public List<SwapTransaction> findByTypeAndTokenPair(@Param("network")String network,
                                                        @Param("token_x_name")String tokenXName,
                                                        @Param("token_y_name")String tokenYName,
                                                        @Param("txn_type")int swapType,
                                                        @Param("swap_seq")int swapSeq,
                                                        @Param("count") int count);

}