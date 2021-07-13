package com.binance.service;

import com.binance.entity.AccountEntity;
import com.binance.entity.TradeRecordEntity;

/**
 * 交易服务
 */
public interface TradeService {
    AccountEntity buy(AccountEntity account, TradeRecordEntity record);

    AccountEntity sale(AccountEntity account, TradeRecordEntity record);
}
