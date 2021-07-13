package com.binance.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StrategyConstant {
    R_BREAKER("R_BREAKER","R_BREAKER策略","R_BREAKER策略")
    ;


    private String type;

    private String name;

    private String desc;
}
