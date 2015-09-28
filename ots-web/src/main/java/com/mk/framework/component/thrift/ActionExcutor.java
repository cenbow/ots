package com.mk.framework.component.thrift;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.springframework.util.MethodInvoker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.mk.framework.AppUtils;
import com.mk.framework.util.CommonUtils;
import com.mk.ots.domain.IBean;
import com.mk.ots.domain.MBean;

public class ActionExcutor implements IActionExcutor.Iface {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(ActionExcutor.class);
	 
	@Override
	public Outbound doAct(String serviceid, String act,
			Map<String, String> inbound, API_VERSION api_version)
			throws TException {
		checkNotNull(serviceid,"serviceid 不允许为null!");
		checkNotNull(act,	   "act 不允许为null!");
		
		Object service = AppUtils.getApplicationContext().getBean(serviceid);
		checkNotNull(act,	   "%s 不存在.");
		
		logger.info("invoke service: Class[{}||{}], Method[{}], Param[{}].",service.getClass().getName(),service.hashCode(),act,"");
		String rtnJsonResult = null;
		String tipMsg = "";
		ResponseStatus status ;
		try {
			Optional<Object> rtnObj = invokeService(service, act, inbound);
			if(rtnObj.isPresent()){
				if(rtnObj.get().getClass().isAssignableFrom(IBean.class)){
					rtnJsonResult = ((IBean)rtnObj.get()).toJson();
				}else{
					rtnJsonResult = new ObjectMapper().writeValueAsString(rtnObj.get());
				}
			}
			status = ResponseStatus.SUCCESS;
		} catch (ClassNotFoundException | NoSuchMethodException
				| InvocationTargetException | IllegalAccessException e) {
			status = ResponseStatus.FAILED;
			tipMsg =  e.getClass().getName()+":"+e.getMessage();
			logger.error("invoke the method[doAct] error.", tipMsg);
		} catch (JsonProcessingException e) {
			status = ResponseStatus.FAILED;
			tipMsg = e.getMessage();
			logger.error("parse the object to json error.", e);
		}
		return new Outbound(status).setTip_message(tipMsg).setResult(rtnJsonResult);
	}

	public Optional<Object> invokeService(Object serviceObj,String act,Map<String, String> inbound) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException{
		if(hasMethod(serviceObj.getClass(),act)){
	        MethodInvoker methodInvoker = new MethodInvoker();
	        methodInvoker.setTargetObject(serviceObj);
	        methodInvoker.setTargetMethod(act);
	        if (inbound != null && inbound.size() > 0) {
	            methodInvoker.setArguments(new Object[]{ new MBean(inbound) });
	        }
	        methodInvoker.prepare();
	        return Optional.fromNullable(methodInvoker.invoke());
		}
		return Optional.absent();
	}
	/**
     * 类中是否包含此方法
     *  TODO 待完善,还需检验参数列表
     * 
     * @param cls 类
     * @param methodName 调用方法名
     * @return 是否存在
     * @see
     */
    private boolean hasMethod(Class cls, String methodName) {
        if (CommonUtils.isNullOrEmpty(methodName)) {
            return false;
        }
        for (Method mtd : cls.getMethods()) {
            if (mtd.getName().equals(methodName) && Modifier.isPublic(mtd.getModifiers())) {
                return true;
            }
        }
        return false;
    }
}
