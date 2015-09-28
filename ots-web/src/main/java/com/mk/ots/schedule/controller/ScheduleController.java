package com.mk.ots.schedule.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.framework.schedule.model.SchedulePlan;
import com.mk.ots.schedule.service.IScheduleService;
import com.mk.ots.ticket.controller.TicketController;

@Controller
@RequestMapping(value="/schedule",method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
public class ScheduleController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IScheduleService iScheduleService;
	
	@RequestMapping("/create")
	public ResponseEntity<SchedulePlan> create(SchedulePlan entity){
		logger.info("添加调度任务. 参数:{}", entity);
		Long id = iScheduleService.insert(entity);
		logger.info("添加调度任务成功. 结果:{}", entity);
		return new ResponseEntity<SchedulePlan>(entity, HttpStatus.OK);
	}

	@RequestMapping("/update")
	public ResponseEntity<SchedulePlan> update(SchedulePlan entity){
		logger.info("修改调度任务. 参数:entity:{}", entity);
		iScheduleService.update(entity);
		logger.info("修改调度任务成功. 结果:{}", entity);
		return new ResponseEntity<SchedulePlan>(entity, HttpStatus.OK);
	}
	
	@RequestMapping("/delete")
	public ResponseEntity<Long> delete(Long id){
		logger.info("删除调度任务. 参数:id:{}", id);
		this.iScheduleService.delete(id);
		logger.info("删除调度任务成功. 结果:{}", id);
		return new ResponseEntity<>(id, HttpStatus.OK);
	}

	@RequestMapping("/list/{id}")
	public ResponseEntity<SchedulePlan> findById(@PathVariable("id") Long id){
		logger.info("查询单个调度任务. 参数:id:{}", id);
		SchedulePlan result = this.iScheduleService.findById(id);
		logger.info("查询单个调度任务成功. 结果:{}", result);
		return new ResponseEntity<SchedulePlan>(result, HttpStatus.OK);
	}

	@RequestMapping("/list")
	public ResponseEntity<List<SchedulePlan>> findAll(SchedulePlan entity){
		logger.info("查询调度任务. 参数:entity:{}", entity);
		List<SchedulePlan> planList = this.iScheduleService.find(entity);
		logger.info("查询调度任务成功. 结果:entity:{}", entity);
		return new ResponseEntity<List<SchedulePlan>>(planList, HttpStatus.OK);
	}

	@RequestMapping("/run")
	public ResponseEntity runNow(Long id){
		logger.info("运行调度任务. 参数:id:{}", id);
		this.iScheduleService.runNow(id);
		logger.info("运行调度任务成功. 结果:id:{}", id);
		return new ResponseEntity(HttpStatus.OK);
	}

	
}
