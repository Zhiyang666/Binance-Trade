package com.binance.entity;

import com.binance.client.constant.BinanceApiConstants;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 时间段价格变动数据
 */
@Data
@Entity
@Table(name = "t_price_change_ticker")
public class PriceChangeTickerEntity {

    //    定义主键id
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * 币种/待兑币种
     */
    private String symbol;

    /**
     * 价格变化
     */
    private BigDecimal priceChange;

    /**
     * 价格变化百分比
     */
    private BigDecimal priceChangePercent;

    /**
     * 时间段权重价格
     */
    private BigDecimal weightedAvgPrice;

    /**
     * 当前价格
     */
    private BigDecimal lastPrice;

    /**
     * 当前价格的成交量
     */
    private BigDecimal lastQty;

    /**
     * 24小时之前的价格
     */
    private BigDecimal openPrice;

    /**
     * 24小时最高价
     */
    private BigDecimal highPrice;


    /**
     * 24小时最低价
     */
    private BigDecimal lowPrice;

    /**
     * 交易物成交量
     */
    private BigDecimal volume;

    /**
     * 被交易物成交量
     */
    private BigDecimal quoteVolume;

    /**
     * 开始时间
     */
    private Long openTime;

    /**
     * 结束时间
     */
    private Long closeTime;

    private Long firstId;

    private Long lastId;

    private Long count;

    @Override
    public String toString() {
        return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE).append("symbol", symbol)
                .append("priceChange", priceChange).append("priceChangePercent", priceChangePercent)
                .append("weightedAvgPrice", weightedAvgPrice).append("lastPrice", lastPrice).append("lastQty", lastQty)
                .append("openPrice", openPrice).append("highPrice", highPrice).append("lowPrice", lowPrice)
                .append("volume", volume).append("quoteVolume", quoteVolume).append("openTime", openTime)
                .append("closeTime", closeTime).append("firstId", firstId).append("lastId", lastId)
                .append("count", count).toString();
    }
}
