package ru.home.sound.stream.helpers;

import java.util.Date;

public class PartData {

    private Date startDate;
    private Date endDate;
    private long durability;
    private byte[] bytes;

    public PartData(Date startDate, Date endDate, byte[] bytes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.durability = (startDate != null && endDate != null) ? endDate.getTime() - startDate.getTime() : 0;
        this.bytes = bytes;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    private long getDurability() {
        return durability;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return "PartData{" +
                "durability=" + getDurability() +
                ", bytes.size=" + (bytes != null ? bytes.length : null) +
                '}';
    }
}
