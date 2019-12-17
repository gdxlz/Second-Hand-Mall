package com.hzf.service.impl;

import com.hzf.entity.TimeRecord;
import com.hzf.entity.TimeRecordExample;
import com.hzf.repository.TimeRecordMapper;
import com.hzf.service.TimeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeRecordServiceImpl implements TimeRecordService {
    @Autowired
    TimeRecordMapper timeRecordMapper;
    //保存插入时间的记录
    @Override
    public void saveTimeRecord(TimeRecord timeRecord) {
        timeRecordMapper.insert(timeRecord);
    }
    //根据goodId 获得 recordId
    @Override
    public int getRecordIdByGoodId(int goodId) {
        TimeRecordExample example = new TimeRecordExample();
        TimeRecordExample.Criteria criteria = example.createCriteria();
        criteria.andGoodIdEqualTo(goodId);
        List<TimeRecord> list = timeRecordMapper.selectByExample(example);
        int recordId = list.get(0).getRecordId();
        return recordId;
    }
    //按主键修改时间记录
    @Override
    public void changeTimeRecord(TimeRecord timeRecord) {
        timeRecordMapper.updateByPrimaryKeySelective(timeRecord);
    }
}
