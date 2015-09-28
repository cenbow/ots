package com.mk.ots.hotel.model;

public class TFacilityModel {
    private Long id;

    private String facname;

    private Integer factype;

    private String binding;

    private Integer facsort;

    private String visible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacname() {
        return facname;
    }

    public void setFacname(String facname) {
        this.facname = facname == null ? null : facname.trim();
    }

    public Integer getFactype() {
        return factype;
    }

    public void setFactype(Integer factype) {
        this.factype = factype;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding == null ? null : binding.trim();
    }

    public Integer getFacsort() {
        return facsort;
    }

    public void setFacsort(Integer facsort) {
        this.facsort = facsort;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible == null ? null : visible.trim();
    }
}