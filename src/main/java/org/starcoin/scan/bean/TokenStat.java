package org.starcoin.scan.bean;

import java.math.BigDecimal;

public class TokenStat {

    private String token;

    private BigDecimal volumeAmount;

    private BigDecimal volume;

    private BigDecimal tvl;

    public TokenStat(String token, BigDecimal volume,BigDecimal volumeAmount, BigDecimal tvl) {
        this.token = token;
        this.volume = volume;
        this.volumeAmount = volumeAmount;
        this.tvl = tvl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setVolumeAmount(BigDecimal volumeAmount) {
        this.volumeAmount = volumeAmount;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setTvl(BigDecimal tvl) {
        this.tvl = tvl;
    }

    public String getToken() {
        return token;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public BigDecimal getTvl() {
        return tvl;
    }

    public BigDecimal getVolumeAmount() {
        return volumeAmount;
    }

}
