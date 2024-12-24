package com.lion.demo.service;

import com.lion.demo.entity.DeliveryInfo;

public interface DeliveryInfoService {
    DeliveryInfo findById(long id);

    DeliveryInfo insertDeliveryInfo(DeliveryInfo deliveryInfo);
}
