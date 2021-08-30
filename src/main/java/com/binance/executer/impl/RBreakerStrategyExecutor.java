package com.binance.executer.impl;

import com.binance.constant.StrategyConstant;
import com.binance.constant.TimeConstant;
import com.binance.entity.AccountEntity;
import com.binance.entity.SubjectEntity;
import com.binance.entity.TradeRecordEntity;
import com.binance.entity.candlestick.CandlestickEntity;
import com.binance.executer.StrategyExecutor;
import com.binance.repository.CandlestickRepository;
import com.binance.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;

@Component
public class RBreakerStrategyExecutor implements StrategyExecutor {

    @Resource(name = "BTCTradeService")
    TradeService tradeService;

    Long nowDate = 1596153600000L;
    @Resource
    CandlestickRepository candlestickRepository;

    private volatile Map<Long, RBreakerRecord> rRecordMap = new HashMap<>();

    public void init() {
        if (rRecordMap.size()==0) {
            PageRequest pageRequest =  PageRequest.of(0,10000);
            Page<CandlestickEntity> candlestickEntities = candlestickRepository.findAllByOpenTimeBetweenAndIntervalEquals(pageRequest,nowDate-TimeConstant.YEAR,nowDate+TimeConstant.YEAR,"1d");
            for(CandlestickEntity entity:candlestickEntities){
                RBreakerRecord record = new RBreakerRecord(entity.getHigh().doubleValue(),entity.getClose().doubleValue(),entity.getLow().doubleValue(),entity.getOpenTime(),TimeConstant.DAY.intValue());
                rRecordMap.put(entity.getOpenTime(),record);
            }
        }

        if(LongPositionAccount==null){
            //做多账户
            LongPositionAccount = new AccountEntity(100000d, 0);
            //做空账户
            ShortPositionAccount = new AccountEntity(100000d, 1);
        }

    }

    //倍率
    Double MULTIPLYING = 10d;
    //止损比率 20%
    Double STOP_LOSS_RATE = 0.2;
    //时间跨度
    Long timeSpan = 1000L * 60 * 60 * 24;
    //coinType
    String coinType = "USDT/BTC";

    @Override
    public Boolean isMe(String tag) {
        if (tag.equals(StrategyConstant.R_BREAKER.getType())) {
            return true;
        }
        return false;
    }

    AccountEntity LongPositionAccount;
    AccountEntity ShortPositionAccount;

    @Override
    public void execute(Collection<SubjectEntity> collection) throws InterruptedException {
        init();
        BlockingQueue<SubjectEntity> blockingQueue = ( BlockingQueue<SubjectEntity>)collection;
        while (true) {

            SubjectEntity entity = blockingQueue.take();
            if (entity==null) {
                Thread.sleep(1000);
                continue;
            }
            Long nowTime = entity.getTime();
            //每15分钟取整
            RBreakerRecord lastRecord = rRecordMap.get(nowTime / timeSpan * timeSpan - timeSpan);
            RBreakerRecord nowRecord = rRecordMap.get(nowTime / timeSpan * timeSpan);

            if (lastRecord == null) {
                //TODO 加载当前数据
                PageRequest pageRequest =  PageRequest.of(0,1);
                Page<CandlestickEntity> candlestickEntities = candlestickRepository.findAllByOpenTimeBetweenAndIntervalEquals(pageRequest,nowDate-TimeConstant.DAY,nowDate,"1d");
                CandlestickEntity cEntity = candlestickEntities.iterator().next();
                lastRecord = new RBreakerRecord(cEntity.getHigh().doubleValue(),cEntity.getClose().doubleValue(),cEntity.getLow().doubleValue(),cEntity.getOpenTime(),TimeConstant.DAY.intValue());
            }
            if (LongPositionAccount.getHoldNum() == 0d && ShortPositionAccount.getHoldNum() == 0d) {
                if (entity.getPrice() >= lastRecord.BB) {
                    //做多
                    settlementTrade(LongPositionAccount, entity, 0);
                } else if (entity.getPrice() <= lastRecord.BS) {
                    //做空
                    settlementTrade(LongPositionAccount, entity, 2);
                }
            } else {
                //设置止损
                if (entity.getPrice() < LongPositionAccount.getHoldPrice() * (1 - STOP_LOSS_RATE / MULTIPLYING)
                        || entity.getPrice() > ShortPositionAccount.getHoldPrice() * (1 + STOP_LOSS_RATE / MULTIPLYING)) {
                    //触发就平仓
                    orderCloseAll(entity);
                }
                // 反转策略: 1.当前高点>观察卖出价 2.当前价格小于反转卖出价
                if (LongPositionAccount.getHoldNum() > 0d && nowRecord.H > lastRecord.WS && entity.getPrice() < lastRecord.RS) {
                    //平仓加做空
                    orderCloseAll(entity);
                    settlementTrade(LongPositionAccount, entity, 2);
                } else if (ShortPositionAccount.getHoldNum() > 0d && nowRecord.L < lastRecord.WB && entity.getPrice() > lastRecord.RB) {
                    //平仓加做多
                    orderCloseAll(entity);
                    settlementTrade(LongPositionAccount, entity, 0);
                }
            }
        }
    }

    /**
     * 全仓交易
     *
     * @param positionAccount
     * @param price
     * @param tradeType
     * @return
     */
    AccountEntity settlementTrade(AccountEntity positionAccount, SubjectEntity entity, Integer tradeType) {
        TradeRecordEntity trade = new TradeRecordEntity();
        trade.setTradeType(tradeType);
        trade.setCoinType(coinType);
        trade.setPrice(entity.getPrice());
        trade.setTime(entity.getTime());
        trade.setReach(1);
        switch (tradeType) {
            case 0:
            case 2:
                trade.setNum(positionAccount.getMoney() / entity.getPrice());
                positionAccount = tradeService.buy(positionAccount, trade);
                break;
            case 1:
                trade.setNum(positionAccount.getHoldNum());
                positionAccount = tradeService.sale(positionAccount, trade);
                break;
            case 3:
                trade.setNum(positionAccount.getHoldMoney() / entity.getPrice());
                positionAccount = tradeService.sale(positionAccount, trade);
                break;
        }
        return positionAccount;
    }


    private void orderCloseAll(SubjectEntity entity) {
        settlementTrade(LongPositionAccount, entity, 1);
        settlementTrade(ShortPositionAccount, entity, 3);
    }

    private class RBreakerRecord {
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

        Double H;
        Double C;
        Double L;

        /**
         * @param H 最高价
         * @param C 最终价格
         * @param L 最低价
         */
        RBreakerRecord(Double H, Double C, Double L, Long time, Integer span) {
            this.H = H;
            this.L = L;
            this.C = C;
            this.P = (H + C + L) / 3;
            this.BB = H + 2 * P - 2 * L;
            this.WS = P + H - L;
            this.RS = 2 * P - L;
            this.RB = 2 * P - H;
            this.WB = P - (H - L);
            this.BS = L - 2 * (H - P);
            this.time = time;
            this.span = span;
        }
    }

    public static void main(String[] args) throws Exception {
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
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
//        Date date = simpleDateFormat.parse("2021-06-08 15:23:00");
//        Date date1 = simpleDateFormat.parse("2021-06-08 15:23:01");
//        System.out.println(date.getTime());
//        System.out.println(date1.getTime());
//        System.out.println(date.getTime() / (1000 * 60 * 15) * (1000 * 60 * 15));
//        System.out.println(date1.getTime() / (1000 * 60 * 15) * (1000 * 60 * 15));

        System.out.println(1596067200000L+TimeConstant.DAY);
    }

}
