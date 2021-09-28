package org.starcoin.scan.repos.entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@Embeddable
public class PoolSwapDayStatId implements Serializable {
    private static final long serialVersionUID = -8001556193829017650L;
    @Column(name = "first_token_name", nullable = false, length = 256)
    private String firstTokenName;
    @Column(name = "second_token_name", nullable = false, length = 256)
    private String secondTokenName;
    @Column(name = "ts", nullable = false)
    private LocalTime ts;

    public LocalTime getTs() {
        return ts;
    }

    public void setTs(LocalTime ts) {
        this.ts = ts;
    }

    public String getSecondTokenName() {
        return secondTokenName;
    }

    public void setSecondTokenName(String secondTokenName) {
        this.secondTokenName = secondTokenName;
    }

    public String getFirstTokenName() {
        return firstTokenName;
    }

    public void setFirstTokenName(String firstTokenName) {
        this.firstTokenName = firstTokenName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secondTokenName, firstTokenName, ts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PoolSwapDayStatId entity = (PoolSwapDayStatId) o;
        return Objects.equals(this.secondTokenName, entity.secondTokenName) &&
                Objects.equals(this.firstTokenName, entity.firstTokenName) &&
                Objects.equals(this.ts, entity.ts);
    }
}