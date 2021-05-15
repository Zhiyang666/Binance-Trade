package com.binance.repository;

import com.binance.entity.PriceChangeTickerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceChangeTickerRepository extends CrudRepository<PriceChangeTickerEntity, Long> {
}
