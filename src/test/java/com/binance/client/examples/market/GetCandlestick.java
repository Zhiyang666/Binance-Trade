package com.binance.client.examples.market;

import com.binance.client.model.enums.CandlestickInterval;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;

import com.binance.client.constant.PrivateConfig;
import com.binance.entity.candlestick.CandlestickEntity;
import com.binance.repository.CandlestickRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

/**
 *
 * 获取k线数据
 */
@SpringBootTest
public class GetCandlestick {

    @Autowired
    CandlestickRepository repository;
    @Test
    void test() {
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

    public static void main(String[] args) {
        System.out.println(new Date(1627516800000L));
        Long nowTime = new Date().getTime();
        Long timeSpan = 1000L*60*60*24;
        System.out.println((nowTime/timeSpan*timeSpan)  );

    }
}
