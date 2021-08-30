package com.binance.repository;

import com.binance.entity.candlestick.CandlestickEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CandlestickRepository extends PagingAndSortingRepository<CandlestickEntity, Long> {
    /**
     *
     * @param pageable 分页
     * @param startOpenTime 开始时间
     * @param endOpenTime 结束时间
     * @param intervalType 数据间隔标识
     * @return
     */
    Page<CandlestickEntity> findAllByOpenTimeBetweenAndIntervalEquals(Pageable pageable, Long startOpenTime, Long endOpenTime, String intervalType);
}
