package com.binance.service.impl;

import com.binance.entity.AccountEntity;
import com.binance.entity.TradeRecordEntity;
import com.binance.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("BTCTradeService")
@Slf4j
public class BTCTradeServiceImpl implements TradeService {
    /**
     * 买入或平空
     *
     * @param account
     * @param record
     * @return
     */
    @Override
    public AccountEntity buy(AccountEntity account, TradeRecordEntity record) {
        //TODO 存储Record
        if (account.getTradeType() == 0) {
            //账户扣减资金
        account.setHoldType(record.getCoinType());
        if (account.getHoldNum() > 0) {
            //计算平均价格
            account.setHoldPrice((record.getPrice() * record.getNum() + account.getHoldPrice() * account.getHoldNum()) / (record.getNum() + account.getHoldNum()));
        } else {
            account.setHoldPrice(record.getPrice());
        }
        account.setHoldNum(account.getHoldNum() + record.getNum());
        account.setHoldMoney(account.getHoldMoney() + record.getNum() * record.getPrice());
        account.setMoney(account.getMoney() - record.getNum() * record.getPrice());
        account.setHoldTime(record.getTime());
        //TODO 交易操作
        //TODO 存储账户记录
            log.info("多头账户买入:{},剩余钱:{},持仓金额:{}", record.getNum(), account.getMoney(), account.getHoldNum() * record.getPrice());

        }
        return account;
    }

    /**
     * 卖出或做空
     *
     * @param account
     * @param record
     * @return
     */
    @Override
    public AccountEntity sale(AccountEntity account, TradeRecordEntity record) {
        //TODO 存储Record
        if (account.getTradeType() == 0) {
        Double holdNum = account.getHoldNum() - record.getNum();
        //当前持有总数量
        account.setHoldNum(holdNum);
        //计算余额，多头账户的卖出
        if (account.getTradeType() == 0) {
            account.setMoney(account.getMoney() + record.getNum() * record.getPrice());
        } else {
            account.setMoney(account.getMoney() + record.getNum() * (2 * account.getHoldPrice() - record.getPrice()));
        }
        if (account.getHoldNum() > 0) {
            //计算平均价格
            account.setHoldPrice((account.getHoldPrice() * account.getHoldNum() - record.getPrice() * record.getNum()) / (account.getHoldNum() - record.getNum()));
        } else {
            account.setHoldType("0");
            account.setHoldPrice(0d);
            account.setHoldTime(null);
        }
        //TODO 交易操作
        //TODO 存储账户记录
            account.setHoldMoney(account.getHoldPrice()*account.getHoldNum());

        log.info("多头账户卖出:{},剩余钱:{},持仓金额:{}", record.getNum(), account.getMoney(), account.getHoldNum() * record.getPrice());
        }
        return account;
    }
}
