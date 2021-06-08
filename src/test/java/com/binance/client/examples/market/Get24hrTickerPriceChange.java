package com.binance.client.examples.market;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;

import com.binance.client.constant.PrivateConfig;
import com.binance.entity.PriceChangeTickerEntity;
import com.binance.repository.PriceChangeTickerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Get24hrTickerPriceChange {
    @Autowired
    PriceChangeTickerRepository repository;
    @Test
    void test() {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        List<PriceChangeTickerEntity> list = syncRequestClient.get24hrTickerPriceChange("BTCUSDT");
        repository.saveAll(list);
        Iterable<PriceChangeTickerEntity> list2 = repository.findAll();
        for (PriceChangeTickerEntity entity:list2){
            System.out.println(entity);
        }
    }
}
