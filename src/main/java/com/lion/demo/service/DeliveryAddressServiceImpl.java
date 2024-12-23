package com.lion.demo.service;

import com.lion.demo.entity.DeliveryAddress;
import com.lion.demo.repository.DeliveryAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {
    @Autowired private DeliveryAddressRepository deliveryAddressRepository;

    @Override
    public DeliveryAddress findById(long id) {
        return deliveryAddressRepository.findById(id).orElse(null);
    }

    @Override
    public DeliveryAddress insertDeliveryAddress(DeliveryAddress deliveryAddress) {
        return deliveryAddressRepository.save(deliveryAddress);
    }
}
