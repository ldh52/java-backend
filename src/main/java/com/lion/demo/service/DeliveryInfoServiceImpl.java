package com.lion.demo.service;

import com.lion.demo.entity.DeliveryInfo;
import com.lion.demo.repository.DeliveryInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    @Autowired private DeliveryInfoRepository deliveryInfoRepository;

    @Override
    public DeliveryInfo findById(long id) {
        return deliveryInfoRepository.findById(id).orElse(null);
    }

    @Override
    public DeliveryInfo insertDeliveryInfo(DeliveryInfo deliveryInfo) {
        return deliveryInfoRepository.save(deliveryInfo);
    }
}
