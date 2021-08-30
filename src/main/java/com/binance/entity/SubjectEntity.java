package com.binance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 交易标的
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectEntity {

    // 价格
    Double price;

    //时间
    Long time;


}
