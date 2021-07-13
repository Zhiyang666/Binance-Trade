package com.binance.service.impl;

import com.binance.entity.AccountEntity;
import com.binance.entity.TradeRecordEntity;
import com.binance.service.TradeService;

public class BTCTradeServiceImpl implements TradeService {
    @Override
    public AccountEntity buy(AccountEntity account, TradeRecordEntity record) {
        //TODO 存储Record
        //账户扣减资金
        account.setHoldType(record.getCoinType());
        if(account.getHoldNum()>0){
            //计算平均价格
            account.setHoldPrice((record.getPrice()*record.getNum()+account.getHoldPrice()*account.getHoldNum())/(record.getNum()+account.getHoldNum()));
        }else {
            account.setHoldPrice(record.getPrice());
        }
        account.setHoldNum(account.getHoldNum()+record.getNum());
        account.setHoldMoney(account.getHoldMoney()+record.getNum()*record.getPrice());
        account.setMoney(account.getMoney()-record.getNum()*record.getPrice());
        account.setHoldTime(record.getTime());
        //TODO 交易操作
        //TODO 存储账户记录

        return account;
    }

    @Override
    public AccountEntity sale(AccountEntity account, TradeRecordEntity record) {
        //TODO 存储Record
        Double holdNum = account.getHoldNum()-record.getNum();
        //当前持有总金额
        Double holdMoney = account.getHoldNum()-record.getNum()*record.getPrice();
        account.setHoldNum(holdNum);
        account.setHoldMoney(holdMoney);
        //计算余额
        if(account.getTradeType()==0){
            account.setMoney(account.getMoney()+record.getNum()*record.getPrice());
        }else {
            account.setMoney(account.getMoney()+record.getNum()*(2*account.getHoldPrice()- record.getPrice()));
        }
        if(account.getHoldNum()>0){
            //计算平均价格
            account.setHoldPrice((account.getHoldPrice()*account.getHoldNum()-record.getPrice()*record.getNum())/(account.getHoldNum()-record.getNum()));
        }else {
            account.setHoldType("0");
            account.setHoldPrice(0d);
            account.setHoldTime(null);
        }
        //TODO 交易操作
        //TODO 存储账户记录
        return account;
    }
}
