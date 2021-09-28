package org.starcoin.scan.repos.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "pool_swap_day_stat")
@Entity
public class PoolSwapDayStat {
    @EmbeddedId
    private PoolSwapDayStatId id;

    @Column(name = "volume_amount", nullable = false, precision = 131089)
    private BigDecimal volumeAmount;

    @Column(name = "volume", precision = 131089)
    private BigDecimal volume;

    @Column(name = "tvl_a_amount", nullable = false, precision = 131089)
    private BigDecimal tvlAAmount;

    @Column(name = "tvl_a", precision = 131089)
    private BigDecimal tvlA;

    @Column(name = "tvl_b_amount", nullable = false, precision = 131089)
    private BigDecimal tvlBAmount;

    @Column(name = "tvl_b", precision = 131089)
    private BigDecimal tvlB;

    public BigDecimal getTvlB() {
        return tvlB;
    }

    public void setTvlB(BigDecimal tvlB) {
        this.tvlB = tvlB;
    }

    public BigDecimal getTvlBAmount() {
        return tvlBAmount;
    }

    public void setTvlBAmount(BigDecimal tvlBAmount) {
        this.tvlBAmount = tvlBAmount;
    }

    public BigDecimal getTvlA() {
        return tvlA;
    }

    public void setTvlA(BigDecimal tvlA) {
        this.tvlA = tvlA;
    }

    public BigDecimal getTvlAAmount() {
        return tvlAAmount;
    }

    public void setTvlAAmount(BigDecimal tvlAAmount) {
        this.tvlAAmount = tvlAAmount;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getVolumeAmount() {
        return volumeAmount;
    }

    public void setVolumeAmount(BigDecimal volumeAmount) {
        this.volumeAmount = volumeAmount;
    }

    public PoolSwapDayStatId getId() {
        return id;
    }

    public void setId(PoolSwapDayStatId id) {
        this.id = id;
    }
}