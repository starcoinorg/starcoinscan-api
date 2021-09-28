package org.starcoin.scan.repos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;

@Table(name = "swap_day_stat")
@Entity
public class SwapDayStat {
    @Id
    @Column(name = "stat_date", nullable = false)
    private LocalTime id;

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

    public LocalTime getId() {
        return id;
    }

    public void setId(LocalTime id) {
        this.id = id;
    }
}