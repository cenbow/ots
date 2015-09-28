package com.mk.ots.ticket.model;

import com.mk.framework.util.RandomModel;

/**
 * @author nolan
 *
 * @param <T>
 */
public class RandomWeightItem<T> extends RandomModel{
	
	private T entity;
	
	/**
	 * @param entity
	 * @param weight
	 */
	public RandomWeightItem(T entity, int weight) {
		this.entity = entity;
		this.weight = weight;
	}

	/**
	 * @return
	 */
	public T getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 */
	public void setEntity(T entity) {
		this.entity = entity;
	}
	
}
