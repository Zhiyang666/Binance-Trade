package com.binance.service.impl;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.constant.PrivateConfig;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.entity.candlestick.CandlestickEntity;
import com.binance.repository.CandlestickRepository;
import com.binance.service.DataCollectService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CandlestickServiceImpl implements DataCollectService {

    @Autowired
    CandlestickRepository repository;
    @Override
    public void execute(String executeType) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        List<CandlestickEntity> candlestickEntities = syncRequestClient.getCandlestick("BTCUSDT", CandlestickInterval.ONE_MINUTE, null, null, 1000);
        repository.saveAll(candlestickEntities);
        Long lastTime =0L;
        for(int i =0;i<526;i++){
            try {
                lastTime = candlestickEntities.get(0).getOpenTime();
                candlestickEntities=  syncRequestClient.getCandlestick("BTCUSDT", CandlestickInterval.ONE_MINUTE, null, candlestickEntities.get(0).getOpenTime(), 1000);
                repository.saveAll(candlestickEntities);
            }catch (Exception e){
                System.out.println("报错,时间为:"+lastTime);
            }

            System.out.println();
        }
    }
}
