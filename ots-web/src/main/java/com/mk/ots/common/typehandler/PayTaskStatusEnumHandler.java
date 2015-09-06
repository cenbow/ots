package com.mk.ots.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.mk.ots.common.enums.PayTaskStatusEnum;

public class PayTaskStatusEnumHandler extends BaseTypeHandler<PayTaskStatusEnum> {
	
	private Class<PayTaskStatusEnum> type;  
    
    private final PayTaskStatusEnum[] enums;
    
    public PayTaskStatusEnumHandler() {
    	this.type = PayTaskStatusEnum.class;  
        this.enums = type.getEnumConstants();  
    }

	@Override
	public void setNonNullParameter(PreparedStatement paramPreparedStatement,
			int paramInt, PayTaskStatusEnum paramT, JdbcType paramJdbcType)
			throws SQLException {
		
		paramPreparedStatement.setInt(paramInt, paramT.getCode());
		
	}

	@Override
	public PayTaskStatusEnum getNullableResult(ResultSet paramResultSet,
			String paramString) throws SQLException {
		
		int i = paramResultSet.getInt(paramString);  
        
        if (paramResultSet.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }
	}

	@Override
	public PayTaskStatusEnum getNullableResult(ResultSet paramResultSet,
			int paramInt) throws SQLException {
		
		int i = paramResultSet.getInt(paramInt);  
        if (paramResultSet.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }  
	}

	@Override
	public PayTaskStatusEnum getNullableResult(
			CallableStatement paramCallableStatement, int paramInt)
			throws SQLException {
		
		int i = paramCallableStatement.getInt(paramInt);  
        if (paramCallableStatement.wasNull()) {  
            return null;  
        } else {  
            // 根据数据库中的code值，定位EnumStatus子类  
            return locateEnumStatus(i);  
        }
	}

	private PayTaskStatusEnum locateEnumStatus(int code) {  
        for(PayTaskStatusEnum status : enums) {  
            if(status.getCode()==(Integer.valueOf(code))) {  
                return status;  
            }  
        }  
        throw new IllegalArgumentException("未知的枚举类型：" + code + ",请核对" + type.getSimpleName());  
    }
}
