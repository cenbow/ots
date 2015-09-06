package com.mk.ots.promo.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.framework.AppUtils;
import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.ots.common.enums.EffectiveTypeEnum;
import com.mk.ots.common.enums.ExpireTypeEnum;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.service.parse.ITicketParse;


/**
 * 优惠券
 * @author nolan
 *
 */
@Component
@DbTable(name="b_promotion", pkey="id")
public class BPromotion extends BizModel<BPromotion> {
	private static final long serialVersionUID = -953540301840569946L;
    
    public static final BPromotion dao = new BPromotion();

	public BPromotion getPromotion(){
        return BPromotion.dao.findById(getLong("promotion"));
    }
	
	public ITicketParse createParseBean(OtaOrder otaOrder) {
		try {
			UTicketDao uTicketDao = AppUtils.getBean(UTicketDao.class);
			Object ob = Class.forName(this.getClassname()).newInstance();
			ITicketParse parse = (ITicketParse) ob;
			if(PromotionTypeEnum.qieke.equals(this.type) || PromotionTypeEnum.yijia.equals(this.type)){
				parse.init(null, this);
			} else {
				parse.init(uTicketDao.findByPromotionId(this.id), this);
			}
			parse.parse(otaOrder);
			return parse;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}	

	private Long id;

    private String name;

    private Date createtime;

    private Date begintime;

    private Date endtime;

    private PromotionTypeEnum type;

    private Boolean isticket;

    private Integer num;

    private String classname;

    private Boolean isota;

    private BigDecimal otapre;

    private Long activitiesid;

    private String pic;

    private Long version;

    private String info;

    private String note;
    
    private Boolean isinstance;
    
    private Integer weight;
    
    /** add by zyj 20150617 */
    private Integer totalnum;
    
    private Integer plannum;
    
    private Integer protype;
    
    private BigDecimal onlineprice;
    
    private BigDecimal offlineprice;
    
    private ExpireTypeEnum expiretype;
    
    private Integer expiredaynum;
    
    private EffectiveTypeEnum effectivetype;
    
    private Integer platformtype;
    private String  sourcecdkey;
    private Long channelid ;
	public String getSourcecdkey() {
		return sourcecdkey;
	}

	public void setSourcecdkey(String sourcecdkey) {
		this.sourcecdkey = sourcecdkey;
	}

	public Long getChannelid() {
		return channelid;
	}

	public void setChannelid(Long channelid) {
		this.channelid = channelid;
	}

	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }


    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }

    public PromotionTypeEnum getType() {
		return type;
	}

	public void setType(PromotionTypeEnum type) {
		this.type = type;
	}

	public Boolean getIsticket() {
		return isticket;
	}

	public void setIsticket(Boolean isticket) {
		this.isticket = isticket;
	}

	public Boolean getIsota() {
		return isota;
	}

	public void setIsota(Boolean isota) {
		this.isota = isota;
	}

	public BigDecimal getOtapre() {
        return otapre;
    }

    public void setOtapre(BigDecimal otapre) {
        this.otapre = otapre;
    }

    public Long getActivitiesid() {
        return activitiesid;
    }

    public void setActivitiesid(Long activitiesid) {
        this.activitiesid = activitiesid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

	public Boolean getIsinstance() {
		return isinstance;
	}

	public void setIsinstance(Boolean isinstance) {
		this.isinstance = isinstance;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(Integer totalnum) {
		this.totalnum = totalnum;
	}

	public Integer getPlannum() {
		return plannum;
	}

	public void setPlannum(Integer plannum) {
		this.plannum = plannum;
	}

	public Integer getProtype() {
		return protype;
	}

	public void setProtype(Integer protype) {
		this.protype = protype;
	}

	public BigDecimal getOnlineprice() {
		return onlineprice;
	}

	public void setOnlineprice(BigDecimal onlineprice) {
		this.onlineprice = onlineprice;
	}

	public BigDecimal getOfflineprice() {
		return offlineprice;
	}

	public void setOfflineprice(BigDecimal offlineprice) {
		this.offlineprice = offlineprice;
	}

	public ExpireTypeEnum getExpiretype() {
		return expiretype;
	}

	public void setExpiretype(ExpireTypeEnum expiretype) {
		this.expiretype = expiretype;
	}

	public Integer getExpiredaynum() {
		return expiredaynum;
	}

	public void setExpiredaynum(Integer expiredaynum) {
		this.expiredaynum = expiredaynum;
	}

	public EffectiveTypeEnum getEffectivetype() {
		return effectivetype;
	}

	public void setEffectivetype(EffectiveTypeEnum effectivetype) {
		this.effectivetype = effectivetype;
	}
	

	public Integer getPlatformtype() {
		return platformtype;
	}

	public void setPlatformtype(Integer platformtype) {
		this.platformtype = platformtype;
	}

	@Override
	public String toString() {
		return "BPromotion [id=" + id + ", name=" + name + ", createtime="
				+ createtime + ", begintime=" + begintime + ", endtime="
				+ endtime + ", type=" + type + ", isticket=" + isticket
				+ ", num=" + num + ", classname=" + classname + ", isota="
				+ isota + ", otapre=" + otapre + ", activitiesid="
				+ activitiesid + ", pic=" + pic + ", version=" + version
				+ ", info=" + info + ", note=" + note + ", isinstance="
				+ isinstance + ", weight=" + weight + ", totalnum=" + totalnum
				+ ", plannum=" + plannum + ", protype=" + protype
				+ ", onlineprice=" + onlineprice + ", offlineprice="
				+ offlineprice + ", expiretype=" + expiretype
				+ ", expiredaynum=" + expiredaynum + ", effectivetype="
				+ effectivetype + ", platformtype=" + platformtype
				+ ", sourcecdkey=" + sourcecdkey + ", channelid=" + channelid
				+ "]";
	}

	
	
}
