package com.binance.service;

import com.binance.entity.TradeRecordEntity;

/**
 * 交易服务
 */
public interface TradeService {
    void buy(TradeRecordEntity record);

    void sale(TradeRecordEntity record);
}
