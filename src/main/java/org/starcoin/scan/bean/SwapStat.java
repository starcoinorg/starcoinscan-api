package org.starcoin.scan.bean;

import java.math.BigDecimal;
import java.util.Date;

public class SwapStat {
    private Date date;

    private BigDecimal volume;

    private BigDecimal tvl;

    public SwapStat(Date date, BigDecimal volume, BigDecimal tvl) {
        this.date = date;
        this.volume = volume;
        this.tvl = tvl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getTvl() {
        return tvl;
    }

    public void setTvl(BigDecimal tvl) {
        this.tvl = tvl;
    }
}
