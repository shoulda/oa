package com.siemens.oa.controller;


import com.siemens.oa.entity.*;
import com.siemens.oa.service.ProjectService;
import com.siemens.oa.annotation.AuthDetec;
import com.siemens.oa.entity.JsonListToWork2;
import com.siemens.oa.entity.Work;
import com.siemens.oa.service.UserService;
import com.siemens.oa.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* Description:
 * \* User: xujin
 * \* Date: 2017/11/22
 * \* Time: 15:01
 * \
 */
@RestController
@RequestMapping("/work")
public class WorkController {
    @Autowired
    private WorkService workService;
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @PostMapping("/insertWork")
    public void insertWork(@RequestBody Work work) {
        workService.insertWork(work);
    }

    @GetMapping("/selectWorkSeries")
    public Series selectWorkSeries(Integer userid, String weekid, Integer weekConut) {
        System.out.print(userid + "-----" + weekid + "-----" + weekConut + "\n");
        Series series = workService.WorkToSeries(userid, weekid, weekConut);
        System.out.print(series);
        return series;
    }

    @GetMapping("/selectWorkByPW")
    public Series selectWorkByPW(Integer projectid, String weekid, Integer weekConut) {
        Series series = workService.ProjectToSeries(projectid, weekid, weekConut);
        System.out.print(series);
        return series;
    }

    @RequestMapping("/selectWorkByScope")
    public JsonListToWork2 selectWorkByScope(HttpSession session, String weekid) {
        String username = session.getAttribute(WebSecurityConfig.SESSION_KEY).toString();
        List<Work> works = workService.selectWorkByWeekId(userService.selectUserIdByName(username), weekid);
        JsonListToWork2 jsonListToWork = workService.WorkToJson2(works, weekid);
        System.out.println(jsonListToWork);
        return jsonListToWork;

    }

    @GetMapping("/selectAllWeekID")
    public List<String> selectAllWeekID() {
        return workService.selectAllWeekID();
    }

    @GetMapping("/selectWork")
    @AuthDetec(authorities = "admin")
    public List<Work> selectWork(HttpSession session) {
        int a = userService.selectUserIdByName(session.getAttribute(WebSecurityConfig.SESSION_KEY).toString());
        System.out.println(session.getAttribute(WebSecurityConfig.SESSION_KEY) + "-----SESSIONKEY-----" + a);
        return workService.selectWork();
    }

    /**
     * 根据weekid获取表格体数据
     *
     * @param weekid
     * @return
     */
    @GetMapping("/getTableData")
    public List getTableData(String weekid) {
        ArrayList<Object> tableData = new ArrayList<>();
        JSONObject userJsonAll = new JSONObject();
        List<User> userList = userService.selectAllUser();
        for (User user : userList) {
            List<Work> works = workService.selectOneWork(user.getUserid(), weekid);
            List<Project> projectList = projectService.selectAllProject();
            JSONObject userJson = new JSONObject();
            userJson.put("name", user.getUsername());
            userJsonAll.put("name", "总和");
            //初始化一个userjson
            for (Project project : projectList) {
                userJson.put(project.getProjectname(), 0);
            }
            for (Work work : works) {
                userJson.replace(projectService.selectProjectById(work.getProjectid()).getProjectname(), work.getHour());
            }
            tableData.add(userJson);
        }
        return tableData;
    }

    @PostMapping("/save")
    public Map<String, Object> modifyWork(@RequestBody String object, HttpSession session) {
        List<Work> work = workService.JsonToWork(object);
        System.out.println("----------收到" + work.size() + "前端work记录---------");

        List<Work> deleteWorks = workService.selectWorkByWeekId(userService.selectUserIdByName(session.getAttribute(WebSecurityConfig.SESSION_KEY).toString()), work.get(0).getWeekid());
        if (deleteWorks.size() > 0) {
            for (Work deleteWork : deleteWorks) {
                if (deleteWork.getM_STATUS().equals(1)) {
                    workService.deleteWork(deleteWork);
                    System.out.println("delete success");
                }
            }
        }
        if (work.size() != 0) {
            for (Work work1 : work) {
                work1.setUserid(userService.selectUserIdByName(session.getAttribute(WebSecurityConfig.SESSION_KEY).toString()));
                Work workTran = workService.selectWorkByUTPS(work1);
                System.out.println("对比数据库结果：" + workTran);
                if (workTran == null) {
                    work1.setM_STATUS(1);
                    workService.insertWork(work1);
                    System.out.println("<*****>此条数据已插入,projectId:" + work1.getProjectid() + ", taskId:" + work1.getTaskid());
                } else ;
            }
            return workService.SubStatus(true, 200, "Save Success!");
        } else {
            return workService.SubStatus(false, 404, "Save Failed! Message is null!");
        }

    }

    @PostMapping("/submit")
    public Map<String, Object> subWork(@RequestBody String object, HttpSession session) {
        System.out.println("<************Submit************>");
        List<Work> work = workService.JsonToWork(object);
        System.out.println("----------收到" + work.size() + "前端work记录---------");

        List<Work> deleteWorks = workService.selectWorkByWeekId(userService.selectUserIdByName(session.getAttribute(WebSecurityConfig.SESSION_KEY).toString()), work.get(0).getWeekid());
        if (deleteWorks.size() > 0) {
            for (Work deleteWork : deleteWorks) {
                if (deleteWork.getM_STATUS().equals(1))
                    workService.deleteWork(deleteWork);
            }
        }
        if (work.size() != 0) {
            for (Work work1 : work) {
                System.out.println(work1);
                work1.setUserid(userService.selectUserIdByName(session.getAttribute(WebSecurityConfig.SESSION_KEY).toString()));
                Work workTran = workService.selectWorkByUTPS(work1);
                System.out.println("对比数据库结果：" + workTran);
                if (workTran == null) {
                    work1.setM_STATUS(0);
                    workService.insertWork(work1);
                    System.out.println("<*****>此条数据已插入,projectId:" + work1.getProjectid() + ", taskId:" + work1.getTaskid());
                } else ;
            }
            return workService.SubStatus(true, 200, "Submit Success!");
        } else {
            return workService.SubStatus(false, 404, "Submit Failed! Message is null!");
        }

    }

}
//
