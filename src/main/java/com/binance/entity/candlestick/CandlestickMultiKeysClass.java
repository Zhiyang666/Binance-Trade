package com.binance.entity.candlestick;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class CandlestickMultiKeysClass implements Serializable {
    private Long openTime;
    private String interval;

}
