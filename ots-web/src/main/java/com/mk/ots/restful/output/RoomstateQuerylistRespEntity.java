package com.mk.ots.restful.output;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * roomstate/querylist房态查询接口返回数据实体类.
 * @author chuaiqing.
 *
 */
public class RoomstateQuerylistRespEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 998257233610237618L;
    
    private Long hotelid;
    private String hotelname;
    private int hotelrulecode;   //20150810 add
    private String repairinfo;
    private String visible;
    private String online;
    private List<Roomtype> roomtype = Lists.newArrayList();
    
    public Long getHotelid() {
        return hotelid;
    }

    public void setHotelid(Long hotelid) {
        this.hotelid = hotelid;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
    }

    public String getRepairinfo() {
        return repairinfo;
    }

    public void setRepairinfo(String repairinfo) {
        this.repairinfo = repairinfo;
    }

    /**
	 * @return the hotelrulecode
	 */
	public int getHotelrulecode() {
		return hotelrulecode;
	}

	/**
	 * @param hotelrulecode the hotelrulecode to set
	 */
	public void setHotelrulecode(int hotelrulecode) {
		this.hotelrulecode = hotelrulecode;
	}

	public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public List<Roomtype> getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(List<Roomtype> roomtype) {
        this.roomtype = roomtype;
    }

    public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}



	/**
     * 房型
     * @author chuaiqing.
     *
     */
    public class Roomtype {
        private Long roomtypeid;
        private Integer bednum;
        private String roomtypename;
        private BigDecimal roomtypeprice;
        //门市价
        private BigDecimal roomtypepmsprice;
        //可售房间数   20150730 add
        private Integer vcroomnum;
        //床信息
        private Bed bed = new Bed();
        
        /**  imike2.5 add  begin  */
        //面积   
        private BigDecimal area;
        //床型 
        private String bedtypename;
        //床宽
        private String bedlength;
        //卫浴类型
        private String bathroomtype; 
        
        
        //洗浴方式
//        private Long bathmode;
        
        private List<Infrastructure> infrastructure = Lists.newArrayList();
        
        private List<Valueaddedfa> valueaddedfa = Lists.newArrayList();
        
        /**  imike2.5 add  end  */
        
        
        private List<Room> rooms = Lists.newArrayList();
        
        private String isfocus = "F";//再次预订用
        
        //mike 3.0 add start
        private String iscashback;
        private BigDecimal cashbackcost;
        // 房型根据可售房间数在C端显示的文本内容
        private String vctxt;
        // mike 3.0 add end 

        // mike 3.1
        private String promoid;
        private String promotype;
        private String isonpromo;
        private String promostarttime;
        private String promoendtime;
        private String promotext;
        private String promoduetime;
        private String promodustartsec;
        private String promoduendsec;
        private Integer promostatus;
        private List<Object> roomtypepic;
        
        public List<Object> getRoomtypepic() {
			return roomtypepic;
		}
		public void setRoomtypepic(List<Object> roomtypepic) {
			this.roomtypepic = roomtypepic;
		}
		public String getPromostarttime() {
			return promostarttime;
		}
		public void setPromostarttime(String promostarttime) {
			this.promostarttime = promostarttime;
		}
		public String getPromoendtime() {
			return promoendtime;
		}
		public void setPromoendtime(String promoendtime) {
			this.promoendtime = promoendtime;
		}
		public String getPromotext() {
			return promotext;
		}
		public void setPromotext(String promotext) {
			this.promotext = promotext;
		}
		public String getPromoduetime() {
			return promoduetime;
		}
		public void setPromoduetime(String promoduetime) {
			this.promoduetime = promoduetime;
		}

        public String getPromodustartsec() {
            return promodustartsec;
        }

        public void setPromodustartsec(String promodustartsec) {
            this.promodustartsec = promodustartsec;
        }

        public String getPromoduendsec() {
            return promoduendsec;
        }

        public void setPromoduendsec(String promoduendsec) {
            this.promoduendsec = promoduendsec;
        }

        public Integer getPromostatus() {
            return promostatus;
        }

        public void setPromostatus(Integer promostatus) {
            this.promostatus = promostatus;
        }

        public String getIsfocus() {
			return isfocus;
		}
		public void setIsfocus(String isfocus) {
			this.isfocus = isfocus;
		}
		public Long getRoomtypeid() {
            return roomtypeid;
        }
        public void setRoomtypeid(Long roomtypeid) {
            this.roomtypeid = roomtypeid;
        }
        public Integer getBednum() {
            return bednum;
        }
        public void setBednum(Integer bednum) {
            this.bednum = bednum;
        }
        public String getRoomtypename() {
            return roomtypename;
        }
        public void setRoomtypename(String roomtypename) {
            this.roomtypename = roomtypename;
        }
        public BigDecimal getRoomtypeprice() {
            return roomtypeprice;
        }
        public void setRoomtypeprice(BigDecimal roomtypeprice) {
            this.roomtypeprice = roomtypeprice;
        }
        public List<Room> getRooms() {
            return rooms;
        }
        public void setRooms(List<Room> rooms) {
            this.rooms = rooms;
        }
        public BigDecimal getRoomtypepmsprice() {
			return roomtypepmsprice;
		}
		public void setRoomtypepmsprice(BigDecimal roomtypepmsprice) {
			this.roomtypepmsprice = roomtypepmsprice;
		}
		public Bed getBed() {
			return bed;
		}
		public void setBed(Bed bed) {
			this.bed = bed;
		}
		public Integer getVcroomnum() {
			return vcroomnum;
		}
		public void setVcroomnum(Integer vcroomnum) {
			this.vcroomnum = vcroomnum;
		}
        public String getVctxt() {
            return vctxt;
        }
        public void setVctxt(String vctxt) {
            this.vctxt = vctxt;
        }
		public BigDecimal getArea() {
			return area;
		}
		public void setArea(BigDecimal area) {
			this.area = area;
		}
		public String getBedtypename() {
			return bedtypename;
		}
		public void setBedtypename(String bedtypename) {
			this.bedtypename = bedtypename;
		}
		public String getBedlength() {
			return bedlength;
		}
		public void setBedlength(String bedlength) {
			this.bedlength = bedlength;
		}
		public String getBathroomtype() {
			return bathroomtype;
		}
		public void setBathroomtype(String bathroomtype) {
			this.bathroomtype = bathroomtype;
		}
		public List<Infrastructure> getInfrastructure() {
			return infrastructure;
		}
		public void setInfrastructure(List<Infrastructure> infrastructure) {
			this.infrastructure = infrastructure;
		}
		public List<Valueaddedfa> getValueaddedfa() {
			return valueaddedfa;
		}
		public void setValueaddedfa(List<Valueaddedfa> valueaddedfa) {
			this.valueaddedfa = valueaddedfa;
		}
		public String getIscashback() {
			return iscashback;
		}
		public void setIscashback(String iscashback) {
			this.iscashback = iscashback;
		}
		public BigDecimal getCashbackcost() {
			return cashbackcost;
		}
		public void setCashbackcost(BigDecimal cashbackcost) {
			this.cashbackcost = cashbackcost;
		}

        public String getPromotype() {
            return promotype;
        }

        public void setPromotype(String promotype) {
            this.promotype = promotype;
        }

        public String getIsonpromo() {
            return isonpromo;
        }

        public void setIsonpromo(String isonpromo) {
            this.isonpromo = isonpromo;
        }

        public String getPromoid() {
            return promoid;
        }

        public void setPromoid(String promoid) {
            this.promoid = promoid;
        }
    }
    
    /**
     * 房间
     * @author chuaiqing.
     *
     */
    public class Room {
        private Long roomid;
        private String roomno;
        private String roomname;
        private String roomstatus;
        private String isselected="F";
        private String haswindow;
        private String floor;
        
        
        //床信息
        //private Bed bed = new Bed();
        
        
        public String getIsselected() {
			return isselected;
		}
		public void setIsselected(String isselected) {
			this.isselected = isselected;
		}
		public Long getRoomid() {
            return roomid;
        }
        public void setRoomid(Long roomid) {
            this.roomid = roomid;
        }
        public String getRoomno() {
            return roomno;
        }
        public void setRoomno(String roomno) {
            this.roomno = roomno;
        }
        public String getRoomname() {
            return roomname;
        }
        public void setRoomname(String roomname) {
            this.roomname = roomname;
        }
        public String getRoomstatus() {
            return roomstatus;
        }
        public void setRoomstatus(String roomstatus) {
            this.roomstatus = roomstatus;
        }
        /*public Bed getBed() {
			return bed;
		}
		public void setBed(Bed bed) {
			this.bed = bed;
		}*/
		public String getHaswindow() {
			return haswindow;
		}
		public void setHaswindow(String haswindow) {
			this.haswindow = haswindow;
		}

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }
    }
    
    /**
     * 床
     * @author chuaiqing.
     *
     */
    public class Bed {
        private Integer count;
        private List<Bedtype> beds = Lists.newArrayList();
        
        public Integer getCount() {
            return count;
        }
        public void setCount(Integer count) {
            this.count = count;
        }
        public List<Bedtype> getBeds() {
            return beds;
        }
        public void setBeds(List<Bedtype> beds) {
            this.beds = beds;
        }
    }
    
    /**
     * 床型
     * @author chuaiqing.
     *
     */
    public class Bedtype {
        private String bedtypename;
        private String bedlength;
        
        public String getBedtypename() {
            return bedtypename;
        }
        public void setBedtypename(String bedtypename) {
            this.bedtypename = bedtypename;
        }
        public String getBedlength() {
            return bedlength;
        }
        public void setBedlength(String bedlength) {
            this.bedlength = bedlength;
        }
    }
    
    
    /**
     * 基础设施
     * @author yubin
     *
     */
    public class Infrastructure {
    	// 基础设施id
    	private Long infrastructureid;
    	// 基础设施名称
    	private String infrastructurename;
    	
    	
		public Long getInfrastructureid() {
			return infrastructureid;
		}
		public void setInfrastructureid(Long infrastructureid) {
			this.infrastructureid = infrastructureid;
		}
		public String getInfrastructurename() {
			return infrastructurename;
		}
		public void setInfrastructurename(String infrastructurename) {
			this.infrastructurename = infrastructurename;
		}
    }
    
    public class Valueaddedfa {
    	// 增值设施id
    	private Long valueaddedfaid;
    	// 增值设施名称
    	private String valueaddedfaname;
    	
		public Long getValueaddedfaid() {
			return valueaddedfaid;
		}
		public void setValueaddedfaid(Long valueaddedfaid) {
			this.valueaddedfaid = valueaddedfaid;
		}
		public String getValueaddedfaname() {
			return valueaddedfaname;
		}
		public void setValueaddedfaname(String valueaddedfaname) {
			this.valueaddedfaname = valueaddedfaname;
		}
    	
    	
    }

    
}
