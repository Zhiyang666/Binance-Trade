package com.binance.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

//账户信息(实时)
@Data
@NoArgsConstructor
public class AccountEntity {
    //余额 usdt
    Double money=0d;

    //持仓种类 未持仓为0
    String holdType="0";

    //持仓数量
    Double holdNum=0d;

    //持仓usdt
    Double holdMoney=0d;

    //持仓价格
    Double holdPrice=0d;

    //持仓时间
    Long holdTime;

    //多:0,空:1
    int tradeType;
    public AccountEntity(Double money,int tradeType){
        this.money=money;
        this.tradeType=tradeType;
    }
}
