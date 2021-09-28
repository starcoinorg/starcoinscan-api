package org.starcoin.scan.repos.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@Embeddable
public class TokenSwapDayStatId implements Serializable {
    private static final long serialVersionUID = 1943694596263790057L;
    @Column(name = "token_name", nullable = false, length = 256)
    private String tokenName;
    @Column(name = "ts", nullable = false)
    private LocalTime ts;

    public LocalTime getTs() {
        return ts;
    }

    public void setTs(LocalTime ts) {
        this.ts = ts;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenName, ts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TokenSwapDayStatId entity = (TokenSwapDayStatId) o;
        return Objects.equals(this.tokenName, entity.tokenName) &&
                Objects.equals(this.ts, entity.ts);
    }
}