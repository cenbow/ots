/**
 * @author he
 *
 */
package com.mk.ots.user.controller;

import com.mk.ots.user.model.TestUser;
import com.mk.ots.user.service.WhiteUserService;
import com.mk.ots.web.ServiceOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WhiteUserController{
	@Autowired
	private WhiteUserService whiteUserService ;
	
	/**
	 * 白名单 查询
	 * @return
	 */
	@RequestMapping("/whiteuser/querylist")
	public ResponseEntity<Map<String,Object>> querylist(){
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			List<TestUser> list = whiteUserService.queryTestUsers();
			result.put("data", list);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	/**
	 * @param record
	 * 白名单 保存
	 */
	@RequestMapping("/whiteuser/save")
	public ResponseEntity<Map<String,Object>> save(TestUser record){
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			int i = whiteUserService.saveTestUser(record);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	/**
	 * @param record
	 * 白名单 更新
	 */
	@RequestMapping("/whiteuser/update")
	public ResponseEntity<Map<String,Object>> update(TestUser record){
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			int i = whiteUserService.updateTestUser(record);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	/**
	 * @param id
	 * 白名单 删除
	 */
	@RequestMapping("/whiteuser/delete")
	public ResponseEntity<Map<String,Object>> delete(Long id){
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			int i = whiteUserService.deleteTestUser(id);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
}