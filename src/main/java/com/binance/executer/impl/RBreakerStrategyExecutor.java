package com.binance.executer.impl;

import com.binance.constant.StrategyConstant;
import com.binance.entity.SubjectEntity;
import com.binance.executer.StrategyExecutor;
import com.binance.service.TradeService;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class RBreakerStrategyExecutor implements StrategyExecutor {

    TradeService tradeService;

    Map<Long,RBreakerRecord>  rRecordMap = new HashMap<>();

    @Override
    public Boolean isMe(String tag) {
        if(tag.equals(StrategyConstant.R_BREAKER.getType())){
            return true;
        }
        return false;
    }

    @Override
    public void execute(Collection<SubjectEntity> collection) {
        Iterator<SubjectEntity> iterator = collection.iterator();
        while (iterator.hasNext()){
            SubjectEntity entity = iterator.next();
            Long nowTime = entity.getTime();
            RBreakerRecord record=   rRecordMap.get(nowTime/(1000*60*15)*(1000*60*15));
            if(record==null){
            }
        }
    }
  

    private class RBreakerRecord{
        //中心价位P = （H + C + L）/3
        Double P;
        //突破买入价 = H + 2P -2L
        Double BB;
        //观察卖出价 = P + H - L
        Double WS;
        //反转卖出价 = 2P - L
        Double RS;
        //反转买入价 = 2P - H
        Double RB;
        //观察买入价 = P - (H - L)
        Double WB;
        //突破卖出价 = L - 2(H - P)
        Double BS;
        //时间
        Long time;
        //跨度(s)
        Integer span;
        /**
         *
         * @param H 最高价
         * @param C 当前价格
         * @param L 最低价
         */
        RBreakerRecord(Double H,Double C,Double L, Long time,Integer span){
            this.P=(H+C+L)/3;
            this.BB =H+2*P-2*L;
            this.WS=P+H-L;
            this.RS=2*P-L;
            this.RB=2*P-H;
            this.WB=P-(H-L);
            this.BS=L-2*(H-P);
            this.time=time;
            this.span=span;
        }
    }
    public static void main(String[] args) throws Exception{
//        BlockingQueue blockingQueue = new LinkedBlockingQueue() ;
//        blockingQueue.add("a");
//        blockingQueue.add("b");
//        blockingQueue.add("c");
//        Iterator iterator = blockingQueue.iterator();
//        int i = 0;
//
//        while (iterator.hasNext()){
//           iterator.next();
//            if(i<=3){
//                blockingQueue.add(i);
//                i++;
//            }
//            System.out.println(blockingQueue.remove());
//
//        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
        Date date = simpleDateFormat.parse("2021-06-08 15:23:00");
        Date date1 = simpleDateFormat.parse("2021-06-08 15:23:01");
        System.out.println(date.getTime());
        System.out.println(date1.getTime());
        System.out.println(date.getTime()/(1000*60*15)*(1000*60*15));
        System.out.println(date1.getTime()/(1000*60*15)*(1000*60*15));

    }

}
