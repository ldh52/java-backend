package com.lion.demo.service;

import com.lion.demo.entity.DeliveryAddress;

public interface DeliveryAddressService {
    DeliveryAddress findById(long id);

    DeliveryAddress insertDeliveryAddress(DeliveryAddress deliveryAddress);
}
