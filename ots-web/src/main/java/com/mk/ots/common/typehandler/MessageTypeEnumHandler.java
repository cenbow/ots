package com.mk.ots.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.mk.ots.message.model.MessageType;

public class MessageTypeEnumHandler extends BaseTypeHandler<MessageType> {  
      
    private Class<MessageType> type;  
       
    private final MessageType[] enums;  
   
    public MessageTypeEnumHandler() {
    	this.type = MessageType.class;  
        this.enums = type.getEnumConstants();  
    }
     
    /** 
     * 设置配置文件设置的转换类以及枚举类内容，供其他方法更便捷高效的实现 
     * @param type 配置文件中设置的转换类 
     */  
    public MessageTypeEnumHandler(Class<MessageType> type) {  
        if (type == null)  
            throw new IllegalArgumentException("Type argument cannot be null");  
        this.type = type;  
        this.enums = type.getEnumConstants();  
        if (this.enums == null)  
            throw new IllegalArgumentException(type.getSimpleName()  
                    + " does not represent an enum type.");  
    }  
  
    public void setNonNullParameter(PreparedStatement ps, int i,  
            MessageType parameter, JdbcType jdbcType) throws SQLException {  
        ps.setString(i, parameter.getId());
    }  
  
    @Override  
    public MessageType getNullableResult(ResultSet rs, String columnName)  
            throws SQLException {  
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放INT类型  
        String i = rs.getString(columnName);
        if (rs.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }  
    }  
  
    @Override  
    public MessageType getNullableResult(ResultSet rs, int columnIndex)  
            throws SQLException {  
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放INT类型  
        String i = rs.getString(columnIndex);  
        if (rs.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }  
    }  
  
    public MessageType getNullableResult(CallableStatement cs, int columnIndex)  
            throws SQLException {  
        // 根据数据库存储类型决定获取类型，本例子中数据库中存放INT类型  
        String i = cs.getString(columnIndex);
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
    private com.mk.ots.message.model.MessageType locateEnumStatus(String code) {  
        for(MessageType status : enums) {  
            if(status.getId().equals(code)) {  
                return status;  
            }  
        }  
        throw new IllegalArgumentException("未知的枚举类型：" + code + ",请核对" + type.getSimpleName());  
    }
}  