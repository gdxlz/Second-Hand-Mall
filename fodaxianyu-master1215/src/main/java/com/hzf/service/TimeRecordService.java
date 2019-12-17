package com.hzf.service;

import com.hzf.entity.TimeRecord;
import org.springframework.stereotype.Service;

@Service
public interface TimeRecordService {
    void saveTimeRecord(TimeRecord timeRecord);
    int getRecordIdByGoodId(int goodId);
    void changeTimeRecord(TimeRecord timeRecord);
}
