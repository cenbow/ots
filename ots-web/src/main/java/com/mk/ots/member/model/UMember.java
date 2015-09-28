package com.mk.ots.member.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.ots.common.enums.CardTypeEnum;
import com.mk.ots.common.enums.FootPrintShareEnum;
import com.mk.ots.common.enums.SexEnum;
import com.mk.ots.hotel.bean.TDistrict;
import com.mk.ots.hotel.bean.ULevel;

/**
 * 消费用户实体
 * @author nolan
 *
 */
@Component
@DbTable(name="u_member", pkey="mid")
public class UMember extends BizModel<UMember> {
	private static final long serialVersionUID = 8829148365301317978L;
	public static final UMember dao = new UMember();
	
	public static final String NORMAL_STATE = "T";// 正常状态
	
	public static final String DISABLE_STATE = "D";// 废弃状态
	
	public static final String ABANDON_STATE = "F";// 禁用状态

    private Long mid;

    private String loginname;

    private String password;

    private Date passwordtime;

    private String paypassword;

    private String name;

    private String sex;

    private Integer birthdayyear;

    private String avatar;

    private String occupation;

    private Long disid;

    private String company;

    private String school;

    private String birthland;

    private String selfintroduction;

    private Integer affectivestate;

    private String interests;

    private String cardtype;

    private String idcard;

    private Date idcardapplytime;

    private String source;

    private String regcode;

    private String cardorg;

    private BigDecimal score1;

    private BigDecimal score2;

    private BigDecimal score3;

    private Long level;

    private String address;

    private String signature;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Long lastpostime;

    private Integer footprint;

    private Long version;

    private String enable;

    private Date createtime;

    private BigDecimal givescore3;

    private String openid;
    
    private String email;

    private String phone;

    private String cpname;

    private String birthday;

    private String phonepic;

    private String personpic;
    
    private String channelid;
    
    private String devicetype;

    private String marketsource;

    private String appversion;

    private String ostype;

    private String osver;

    private String weixinname;

    private String comefrom;
    
    private String comefromtype;

    private Long hotelid;
    
    private String unionid;
    
    private String regostype;
    
    public String getRegostype() {
		return regostype;
	}

	public void setRegostype(String regostype) {
		this.regostype = regostype;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getComefromtype() {
		return comefromtype;
	}

	public void setComefromtype(String comefromtype) {
		this.comefromtype = comefromtype;
	}

	public Long getHotelid() {
		return hotelid;
	}

	public void setHotelid(Long hotelid) {
		this.hotelid = hotelid;
	}

	
    
    public Long getId(){
    	return mid;
    }
    
    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname == null ? null : loginname.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Date getPasswordtime() {
        return passwordtime;
    }

    public void setPasswordtime(Date passwordtime) {
        this.passwordtime = passwordtime;
    }

    public String getPaypassword() {
        return paypassword;
    }

    public void setPaypassword(String paypassword) {
        this.paypassword = paypassword == null ? null : paypassword.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Integer getBirthdayyear() {
        return birthdayyear;
    }

    public void setBirthdayyear(Integer birthdayyear) {
        this.birthdayyear = birthdayyear;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation == null ? null : occupation.trim();
    }

    public Long getDisid() {
        return disid;
    }

    public void setDisid(Long disid) {
        this.disid = disid;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school == null ? null : school.trim();
    }

    public String getBirthland() {
        return birthland;
    }

    public void setBirthland(String birthland) {
        this.birthland = birthland == null ? null : birthland.trim();
    }

    public String getSelfintroduction() {
        return selfintroduction;
    }

    public void setSelfintroduction(String selfintroduction) {
        this.selfintroduction = selfintroduction == null ? null : selfintroduction.trim();
    }

    public Integer getAffectivestate() {
        return affectivestate;
    }

    public void setAffectivestate(Integer affectivestate) {
        this.affectivestate = affectivestate;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests == null ? null : interests.trim();
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype == null ? null : cardtype.trim();
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard == null ? null : idcard.trim();
    }

    public Date getIdcardapplytime() {
        return idcardapplytime;
    }

    public void setIdcardapplytime(Date idcardapplytime) {
        this.idcardapplytime = idcardapplytime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getRegcode() {
        return regcode;
    }

    public void setRegcode(String regcode) {
        this.regcode = regcode == null ? null : regcode.trim();
    }

    public String getCardorg() {
        return cardorg;
    }

    public void setCardorg(String cardorg) {
        this.cardorg = cardorg == null ? null : cardorg.trim();
    }

    public BigDecimal getScore1() {
        return score1;
    }

    public void setScore1(BigDecimal score1) {
        this.score1 = score1;
    }

    public BigDecimal getScore2() {
        return score2;
    }

    public void setScore2(BigDecimal score2) {
        this.score2 = score2;
    }

    public BigDecimal getScore3() {
        return score3;
    }

    public void setScore3(BigDecimal score3) {
        this.score3 = score3;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature == null ? null : signature.trim();
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Long getLastpostime() {
        return lastpostime;
    }

    public void setLastpostime(Long lastpostime) {
        this.lastpostime = lastpostime;
    }

    public Integer getFootprint() {
        return footprint;
    }

    public void setFootprint(Integer footprint) {
        this.footprint = footprint;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable == null ? null : enable.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public BigDecimal getGivescore3() {
        return givescore3;
    }

    public void setGivescore3(BigDecimal givescore3) {
        this.givescore3 = givescore3;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getCpname() {
        return cpname;
    }

    public void setCpname(String cpname) {
        this.cpname = cpname == null ? null : cpname.trim();
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday == null ? null : birthday.trim();
    }

    public String getPhonepic() {
        return phonepic;
    }

    public void setPhonepic(String phonepic) {
        this.phonepic = phonepic == null ? null : phonepic.trim();
    }

    public String getPersonpic() {
        return personpic;
    }

    public void setPersonpic(String personpic) {
        this.personpic = personpic == null ? null : personpic.trim();
    }

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getMarketsource() {
		return marketsource;
	}

	public void setMarketsource(String marketsource) {
		this.marketsource = marketsource;
	}

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	public String getOsver() {
		return osver;
	}

	public void setOsver(String osver) {
		this.osver = osver;
	}

	public String getWeixinname() {
		return weixinname;
	}

	public void setWeixinname(String weixinname) {
		this.weixinname = weixinname;
	}

	public String getComefrom() {
		return comefrom;
	}

	public void setComefrom(String comefrom) {
		this.comefrom = comefrom;
	}

	@Override
	public String toString() {
		return "UMember [mid=" + mid + ", loginname=" + loginname
				+ ", password=" + password + ", passwordtime=" + passwordtime
				+ ", paypassword=" + paypassword + ", name=" + name + ", sex="
				+ sex + ", birthdayyear=" + birthdayyear + ", avatar=" + avatar
				+ ", occupation=" + occupation + ", disid=" + disid
				+ ", company=" + company + ", school=" + school
				+ ", birthland=" + birthland + ", selfintroduction="
				+ selfintroduction + ", affectivestate=" + affectivestate
				+ ", interests=" + interests + ", cardtype=" + cardtype
				+ ", idcard=" + idcard + ", idcardapplytime=" + idcardapplytime
				+ ", source=" + source + ", regcode=" + regcode + ", cardorg="
				+ cardorg + ", score1=" + score1 + ", score2=" + score2
				+ ", score3=" + score3 + ", level=" + level + ", address="
				+ address + ", signature=" + signature + ", longitude="
				+ longitude + ", latitude=" + latitude + ", lastpostime="
				+ lastpostime + ", footprint=" + footprint + ", version="
				+ version + ", enable=" + enable + ", createtime=" + createtime
				+ ", givescore3=" + givescore3 + ", openid=" + openid
				+ ", email=" + email + ", phone=" + phone + ", cpname="
				+ cpname + ", birthday=" + birthday + ", phonepic=" + phonepic
				+ ", personpic=" + personpic + ", channelid=" + channelid
				+ ", devicetype=" + devicetype + ", marketsource="
				+ marketsource + ", appversion=" + appversion + ", ostype="
				+ ostype + ", osver=" + osver + ", weixinname=" + weixinname
				+ ", comefrom=" + comefrom + ", comefromtype=" + comefromtype
				+ ", hotelid=" + hotelid + ", unionid=" + unionid + "]";
	}
	
}