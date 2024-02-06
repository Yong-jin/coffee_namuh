package com.cafeapp.service.menu.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cafeapp.dao.menu.MenuDAO;
import com.cafeapp.dto.menu.Menu;
import com.cafeapp.service.menu.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	MenuDAO menuDAO;

	@Override
	public List<Menu> ShowMenuList() {
		List<Menu> menuList = menuDAO.ShowMenuList();
		return menuList;
	}
}