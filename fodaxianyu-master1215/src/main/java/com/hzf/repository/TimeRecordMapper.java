package com.hzf.repository;

import com.hzf.entity.TimeRecord;
import com.hzf.entity.TimeRecordExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeRecordMapper {
    int countByExample(TimeRecordExample example);

    int deleteByExample(TimeRecordExample example);

    int deleteByPrimaryKey(Integer recordId);

    int insert(TimeRecord record);

    int insertSelective(TimeRecord record);

    List<TimeRecord> selectByExample(TimeRecordExample example);

    TimeRecord selectByPrimaryKey(Integer recordId);

    int updateByExampleSelective(@Param("record") TimeRecord record, @Param("example") TimeRecordExample example);

    int updateByExample(@Param("record") TimeRecord record, @Param("example") TimeRecordExample example);

    int updateByPrimaryKeySelective(TimeRecord record);

    int updateByPrimaryKey(TimeRecord record);
}