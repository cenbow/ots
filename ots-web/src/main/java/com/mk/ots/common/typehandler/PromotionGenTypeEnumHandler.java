package com.mk.ots.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.mk.ots.activity.model.PromotionGenTypeEnum;


public class PromotionGenTypeEnumHandler extends BaseTypeHandler<PromotionGenTypeEnum> {  
      
    private Class<PromotionGenTypeEnum> type;  
       
    private final PromotionGenTypeEnum[] enums;  
   
    public PromotionGenTypeEnumHandler() {
    	this.type = PromotionGenTypeEnum.class;  
        this.enums = type.getEnumConstants();  
    }
     
    public void setNonNullParameter(PreparedStatement ps, int i,  
            PromotionGenTypeEnum parameter, JdbcType jdbcType) throws SQLException {  
        // baseTypeHandler已经帮我们做了parameter的null判断  
        ps.setInt(i, parameter.getType());  
          
    }  
  
    @Override  
    public PromotionGenTypeEnum getNullableResult(ResultSet rs, String columnName)  
            throws SQLException {  
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放INT类型  
        int i = rs.getInt(columnName);  
           
        if (rs.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }  
    }  
  
    @Override  
    public PromotionGenTypeEnum getNullableResult(ResultSet rs, int columnIndex)  
            throws SQLException {  
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放INT类型  
        int i = rs.getInt(columnIndex);  
        if (rs.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }  
    }  
  
    public PromotionGenTypeEnum getNullableResult(CallableStatement cs, int columnIndex)  
            throws SQLException {  
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放INT类型  
        int i = cs.getInt(columnIndex);  
        if (cs.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }  
    }  
      
    /** 
     * 枚举类型转换，由于构造函数获取了枚举的子类enums，让遍历更加高效快捷 
     * @param code 数据库中存储的自定义code属性 
     * @return code对应的枚举类 
     */  
    private PromotionGenTypeEnum locateEnumStatus(int code) {  
        for(PromotionGenTypeEnum status : enums) {  
            if(status.getType()==(Integer.valueOf(code))) {  
                return status;  
            }  
        }  
        throw new IllegalArgumentException("未知的枚举类型：" + code + ",请核对" + type.getSimpleName());  
    }
}  