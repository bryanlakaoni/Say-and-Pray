package com.example.sayandpray.model;

public class Product {
    private int idDoa;
    private String namaDoa;
    private String detailDoa;
    private boolean isFavDoa;

    public Product(int idDoa, String namaDoa, String detailDoa, boolean isFavDoa) {
        this.idDoa = idDoa;
        this.namaDoa = namaDoa;
        this.detailDoa = detailDoa;
        this.isFavDoa = isFavDoa;
    }

    public String getDetailDoa() {
        return detailDoa;
    }

    public String getNamaDoa() {
        return namaDoa;
    }

    public int getIdDoa(){
        return idDoa;
    }

    public boolean getIsFavDoa(){
        return isFavDoa;
    }

}
