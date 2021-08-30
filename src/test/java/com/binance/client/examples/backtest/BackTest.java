package com.binance.client.examples.backtest;

import com.binance.constant.TimeConstant;
import com.binance.entity.SubjectEntity;
import com.binance.entity.candlestick.CandlestickEntity;
import com.binance.executer.StrategyExecutor;
import com.binance.repository.CandlestickRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootTest
public class BackTest {
    @Resource
    StrategyExecutor rBreakerStrategyExecutor;

    @Resource
    CandlestickRepository candlestickRepository;

    Long nowDate = 1596153600000L;

    @Test
    void test() {
        BlockingQueue<SubjectEntity> blockingQueue = new LinkedBlockingQueue<>() ;
        new Thread(){
           public void run(){
                try {
                    rBreakerStrategyExecutor.execute(blockingQueue);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();


        while (nowDate<1596153600000L+TimeConstant.YEAR){
            PageRequest pageRequest =  PageRequest.of(0,10000);
            Page<CandlestickEntity> candlestickEntities = candlestickRepository.findAllByOpenTimeBetweenAndIntervalEquals(pageRequest,nowDate,nowDate+TimeConstant.DAY,"1m");
            nowDate=nowDate+TimeConstant.DAY;
            for(CandlestickEntity entity:candlestickEntities){
                SubjectEntity subjectEntity = new SubjectEntity(entity.getOpen().doubleValue(),entity.getOpenTime());
                blockingQueue.offer(subjectEntity);
            }

        }


    }
}
