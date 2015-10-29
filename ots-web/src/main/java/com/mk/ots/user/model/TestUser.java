package com.mk.ots.user.model;

import java.io.Serializable;

public class TestUser implements Serializable{
	private static final long serialVersionUID = -8339760604227040474L;

	private Long id;

    private String phone;

    private String backupfield1;

    private String backupfield2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getBackupfield1() {
        return backupfield1;
    }

    public void setBackupfield1(String backupfield1) {
        this.backupfield1 = backupfield1 == null ? null : backupfield1.trim();
    }

    public String getBackupfield2() {
        return backupfield2;
    }

    public void setBackupfield2(String backupfield2) {
        this.backupfield2 = backupfield2 == null ? null : backupfield2.trim();
    }
}