package org.starcoin.scan.repos.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "token_swap_day_stat")
@Entity
public class TokenSwapDayStat {
    @EmbeddedId
    private TokenSwapDayStatId id;

    @Column(name = "volume_amount", nullable = false, precision = 131089)
    private BigDecimal volumeAmount;

    @Column(name = "volume", precision = 131089)
    private BigDecimal volume;

    @Column(name = "tvl_amount", nullable = false, precision = 131089)
    private BigDecimal tvlAmount;

    @Column(name = "tvl", precision = 131089)
    private BigDecimal tvl;

    public BigDecimal getTvl() {
        return tvl;
    }

    public void setTvl(BigDecimal tvl) {
        this.tvl = tvl;
    }

    public BigDecimal getTvlAmount() {
        return tvlAmount;
    }

    public void setTvlAmount(BigDecimal tvlAmount) {
        this.tvlAmount = tvlAmount;
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

    public TokenSwapDayStatId getId() {
        return id;
    }

    public void setId(TokenSwapDayStatId id) {
        this.id = id;
    }
}