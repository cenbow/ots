package com.mk.ots.promo.model;

public class BPromotionRule {
    private String id;

    private Long promotionid;

    private String rulecode;

    private String ruleformula;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Long getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(Long promotionid) {
        this.promotionid = promotionid;
    }

    public String getRulecode() {
        return rulecode;
    }

    public void setRulecode(String rulecode) {
        this.rulecode = rulecode == null ? null : rulecode.trim();
    }

    public String getRuleformula() {
        return ruleformula;
    }

    public void setRuleformula(String ruleformula) {
        this.ruleformula = ruleformula == null ? null : ruleformula.trim();
    }
}