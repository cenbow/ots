/**
 * 
 */
package com.mk.ots.mapper;

import com.mk.ots.promoteconfig.model.TPromoteConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface TPromoteConfigMapper {

	public TPromoteConfig queryByCityLevel(Integer cityLevel);
}
