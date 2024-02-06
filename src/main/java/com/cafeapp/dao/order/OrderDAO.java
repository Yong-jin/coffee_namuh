package com.cafeapp.dao.order;

import java.util.List;

import com.cafeapp.dto.order.OrderList;
import com.cafeapp.dto.order.OrderSearchCondition;

public interface OrderDAO {

	//주문리스트 목록 및 검색
	public List<OrderList> findOrderListBySearchCondition(OrderSearchCondition orderSearchCondition);
}