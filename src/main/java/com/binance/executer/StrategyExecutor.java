package com.binance.executer;

import com.binance.entity.SubjectEntity;

import java.util.Collection;

/**
 * 策略执行器
 */
public interface StrategyExecutor {
    Boolean isMe(String tag);

    void execute(Collection<SubjectEntity> collection) throws InterruptedException;
}
