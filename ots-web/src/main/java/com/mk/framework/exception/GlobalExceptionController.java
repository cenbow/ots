package com.mk.framework.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.google.common.collect.Maps;

/**
 * 系统例外处理
 *
 * @author nolan.
 *
 */
@ControllerAdvice
public class GlobalExceptionController {
	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

	@ExceptionHandler(MyException.class)
	public ResponseEntity<Map<String, Object>> handleCustomException(MyException ex) {
		Map<String, Object> errorMap = Maps.newHashMap();
		if (ex.getMyErrorEnum().equals(MyErrorEnum.customError) || ex.getMyErrorEnum().equals(MyErrorEnum.errorParm)) {
			errorMap.put("errmsg", ex.getLocalizedMessage());
		} else {
			errorMap.put("errmsg", ex.getMyErrorEnum().getErrorMsg());
		}
		errorMap.put("success", false);
		errorMap.put("errcode", ex.getMyErrorEnum().getErrorCode());
		GlobalExceptionController.logger.error("哎呦！异常::" + ex.getLocalizedMessage(), ex);
		return new ResponseEntity<Map<String, Object>>(errorMap, HttpStatus.OK);
	}

	@ExceptionHandler(com.mk.pms.exception.PmsException.class)
	public ResponseEntity<Map<String, Object>> handlePmsException(com.mk.pms.exception.PmsException ex) {
		Map<String, Object> errorMap = Maps.newHashMap();
		errorMap.put("success", false);
		errorMap.put("errcode", ex.getErrorCode());
		errorMap.put("errmsg", ex.getErrorMessage());
		GlobalExceptionController.logger.error("PMS异常::" + ex.getLocalizedMessage(), ex);

		return new ResponseEntity<Map<String, Object>>(errorMap, HttpStatus.OK);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
		GlobalExceptionController.logger.error(e.getMessage(), e);

		Map<String, Object> errorMap = Maps.newHashMap();
		errorMap.put("errmsg", e.getMessage());

		return new ResponseEntity<Map<String, Object>>(errorMap, HttpStatus.EXPECTATION_FAILED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllException(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<String>(ex.getLocalizedMessage(), HttpStatus.OK);
	}

}