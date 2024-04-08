package com.example.demo.custom;

import org.springframework.batch.item.file.transform.LineAggregator;

import com.example.demo.dto.OneDto;

public class CustomPssThroughtLineAggregator<T> implements LineAggregator<T>{

	@Override
	public String aggregate(T item) {
		
		if(item instanceof OneDto) {
			return item.toString()+"_item";
		}
		
		return item.toString();
	}	
}
