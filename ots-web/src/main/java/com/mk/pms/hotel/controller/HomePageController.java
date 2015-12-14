package com.mk.pms.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.ots.search.service.IPromoSearchService;

@Controller
@RequestMapping(value = "/homepage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class HomePageController {
	@Autowired
	private IPromoSearchService promoService;
	
	@RequestMapping("/themes")
	public void listThemes()
	{
		
	}
}
