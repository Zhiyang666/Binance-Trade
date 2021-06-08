package com.binance.entity;

import lombok.Data;

/**
 * 交易记录
 */
@Data
public class TradeRecordEntity {
    // 价格
    Double price;

    // 时间
    Long time;

    // 币种
    String coinType;

    // 数量
    Double count;

    // 交易类型 0.开多 1.平多 2.开空 3.平空
    Integer tradeType;

    // 交易是否达成 对于限价单来说可能未达成：0 已达成：1
    Integer reach=0;

}
