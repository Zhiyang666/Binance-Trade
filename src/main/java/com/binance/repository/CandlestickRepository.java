package com.binance.repository;

import com.binance.entity.candlestick.CandlestickEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandlestickRepository extends CrudRepository<CandlestickEntity, Long> {
}
