package com.mk.ots.restful.output;

import java.io.Serializable;
import java.util.List;

/**
 * search/positions 查询位置区域返回实体类.
 * 
 * @author LYN.
 *
 */
public class SearchPositionsCoordinateRespEntity implements Serializable {

	private static final long serialVersionUID = 1L;

		private Long id;
		private String type;
		private String tname;
		private String name;		
		private String coordinates;		
		private List<Child>  child;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(String coordinates) {
			this.coordinates = coordinates;
		}

		public String getTname() {
			return tname;
		}

		public void setTname(String tname) {
			this.tname = tname;
		}

		public List<Child> getChild() {
			return child;
		}

		public void setChild(List<Child> child) {
			this.child = child;
		}


		public class Child{
			private Long id;
			private Long pid;
			private Long cid;
			private String cName;
			private String cCoordinates;
			public Long getId() {
				return id;
			}
			public void setId(Long id) {
				this.id = id;
			}
			public Long getPid() {
				return pid;
			}
			public void setPid(Long pid) {
				this.pid = pid;
			}
			public Long getCid() {
				return cid;
			}
			public void setCid(Long cid) {
				this.cid = cid;
			}
			public String getcName() {
				return cName;
			}
			public void setcName(String cName) {
				this.cName = cName;
			}
			public String getcCoordinates() {
				return cCoordinates;
			}
			public void setcCoordinates(String cCoordinates) {
				this.cCoordinates = cCoordinates;
			}
		}
}
