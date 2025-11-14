package edu.espe.springlab.dto;

public class StudentStatsResponse {
    private long total;
    private long activos;
    private long inactivos;

    public StudentStatsResponse() {}

    public StudentStatsResponse(long total, long activos, long inactivos) {
        this.total = total;
        this.activos = activos;
        this.inactivos = inactivos;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getActivos() {
        return activos;
    }

    public void setActivos(long activos) {
        this.activos = activos;
    }

    public long getInactivos() {
        return inactivos;
    }

    public void setInactivos(long inactivos) {
        this.inactivos = inactivos;
    }
}
