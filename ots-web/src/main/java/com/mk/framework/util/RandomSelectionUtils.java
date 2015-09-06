package com.mk.framework.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class RandomSelectionUtils {

	/**
	 * 随机抽取集合中某几条记录
	 * @param list
	 * @param count
	 * @return
	 */
	public static <T> List<T> randomSelect(List<? extends RandomModel> list, int count){
		if (list == null || list.size() <= count || count <= 0){
			return (List<T>) list;
	    }

        //计算权重总和
        int totalWeights = 0;
        for (int i = 0; i < list.size(); i++){
            totalWeights += list.get(i).getWeight() + 1;  //权重+1，防止为0情况。
        }

	    //随机赋值权重
	    List<Map.Entry<Integer, Integer>> wList = Lists.newArrayList(); //第一个int为list下标索引、第一个int为权重排序值
	    int[] randomNum = randomArray(0,totalWeights-1,list.size());
	    for (int i = 0; i < list.size(); i++) {
	    	int w = (list.get(i).getWeight() + 1) + randomNum[i];   // （权重+1） + 从0到（总权重-1）的随机数
	    	Map<Integer, Integer> newMap = ImmutableMap.of(i, w);
	    	wList.addAll(newMap.entrySet());
	    }
	    
	    Comparator<Map.Entry<Integer, Integer>> valuecmp = new Comparator<Map.Entry<Integer, Integer>>() {
	        @Override
	        public int compare(Map.Entry<Integer, Integer> left, Map.Entry<Integer, Integer> right) {
	            return right.getValue().compareTo(left.getValue());
	        }
	    };
	    Collections.sort(wList, valuecmp);
	    
	    List<T> rtnList = Lists.newArrayList();
	    for (int i = 0; i < count; i++){
	    	rtnList.add((T) list.get(wList.get(i).getKey()));
	    }
	    return rtnList;
	}
	
	 
	/**
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	private static int[] randomArray(int min,int max,int n){  
	    int len = max-min+1;  
	    if(max < min || n > len){  
	        return null;  
	    }  

	    //初始化给定范围的待选数组  
	    int[] source = new int[len];  
        for (int i = min; i < min+len; i++){  
	        source[i-min] = i;  
        }  
	         
        int[] result = new int[n];  
        Random rd = new Random();  
        int index = 0;  
        for (int i = 0; i < result.length; i++) {  
           //待选数组0到(len-2)随机一个下标  
           index = Math.abs(rd.nextInt() % len--);  
           //将随机到的数放入结果集  
           result[i] = source[index];  
           //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换  
           source[index] = source[len];  
        }  
        return result;  
	}  
}
