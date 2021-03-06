package com.siemens.oa.service;

import com.siemens.oa.entity.JsonListToWork;
import com.siemens.oa.entity.JsonListToWork2;
import com.siemens.oa.entity.Series;
import com.siemens.oa.entity.Work;
import org.apache.ibatis.annotations.Param;


import java.awt.*;
import java.util.List;
import java.util.Map;

public interface WorkService {

    void insertWork(Work work);

    void deleteWork(Work work);

    void updateWork(Work work);

    List<Work> selectWork();

    Work selectWorkByUTPS(Work work);

    List<Work> selectWorkByWeekId(Integer userid, String weekid);

    List<Work> JsonToWork(String JsonStr);

    JsonListToWork2 WorkToJson(List<Work> works, String weekid);

    JsonListToWork2 WorkToJson2(List<Work> works, String weekid);

    Map<String, Object> SubStatus(boolean success, Integer code, String message);

    Series WorkToSeries(Integer userid, String weekid, String weekConut);

    Series ProjectToSeries(Integer projectid, String weekid, String weekConut);

    List<Work> selectOneWork(Integer userid, String weekid);

    List<String> selectAllWeekID();

}