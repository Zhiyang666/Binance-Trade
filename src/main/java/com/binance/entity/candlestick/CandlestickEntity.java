package com.binance.entity.candlestick;

import com.binance.client.constant.BinanceApiConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * k线图对象
 */
@Data
@Entity
@Table(name = "t_candlestick")
@IdClass(CandlestickMultiKeysClass.class)
public class CandlestickEntity implements Serializable {

    @Id
    private Long openTime;

    //设置时间间隔，防止close_time出现细微偏差影响查询
    @Id
    private String interval;

    private Long closeTime;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    //成交量
    private BigDecimal volume;

    //委托挂单量
    private BigDecimal quoteAssetVolume;

    //成交笔数
    private Integer numTrades;

    //多头成交量
    private BigDecimal takerBuyBaseAssetVolume;

    //多头挂单量
    private BigDecimal takerBuyQuoteAssetVolume;

    private BigDecimal ignore;

    @Override
    public String toString() {
        return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE).append("openTime", openTime)
                .append("open", open).append("high", high).append("low", low).append("close", close)
                .append("volume", volume).append("closeTime", closeTime).append("quoteAssetVolume", quoteAssetVolume)
                .append("numTrades", numTrades).append("takerBuyBaseAssetVolume", takerBuyBaseAssetVolume)
                .append("takerBuyQuoteAssetVolume", takerBuyQuoteAssetVolume).append("ignore", ignore).toString();
    }

}
