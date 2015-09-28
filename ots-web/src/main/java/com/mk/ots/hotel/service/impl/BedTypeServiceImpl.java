/**
 * 
 */
package com.mk.ots.hotel.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.bean.TBedType;
import com.mk.ots.hotel.service.BedTypeService;
import com.mk.ots.mapper.BedTypeMapper;

/**
 * @author yub
 *
 */
@Service
public class BedTypeServiceImpl implements BedTypeService{
	
	@Autowired
	private BedTypeMapper bedTypeMapper;

	@Override
	public List<TBedType> getRoombedtype() {
		return bedTypeMapper.getRoombedtype();
	}

}
