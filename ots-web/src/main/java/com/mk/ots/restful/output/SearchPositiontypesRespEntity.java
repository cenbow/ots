package com.mk.ots.restful.output;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Lists;

/**
 * search/positiontypes 查询位置区域类型返回实体类.
 * @author LYN.
 *
 */
public class SearchPositiontypesRespEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    
    private Long id;
    private String typename;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	
	public static void main (String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        	SearchPositiontypesRespEntity resp = new SearchPositiontypesRespEntity();
            System.out.println(objectMapper.writeValueAsString(resp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
