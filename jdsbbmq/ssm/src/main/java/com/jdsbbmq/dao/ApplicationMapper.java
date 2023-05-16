package com.jdsbbmq.dao;

import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplicationMapper {

    //查询课程已选人数
    Integer selectCourseApplicationCount(@Param("course_id") String course_id);

    //添加申请
    Integer insert(@Param("application") Application application);

    //返回此申请被驳回的原因
    String selectCourseApplicationReason(@Param("application_id") String application_id);

    //查询要审批的申请
    List<Application> applyList(@Param("user_id") String id);

    //删除申请
    Integer deleteApplication(@Param("application_id") String application_id);

    //修改申请
    Integer updateApplication(@Param("application") Application application);

    //申请通过，申请进度+1
    Integer updateApplicationPass(@Param("application_id") String application_id, @Param("course_id") String course_id);

    //申请驳回，申请进度+1，finish=-1，申请失败
    Integer updateApplicationFail(@Param("application_id") String application_id);

    //还原申请进度
    Integer updateApplicationProgress(@Param("application_id") String application_id);

    //获取全部申请信息
    List<Course> applyListAll();

    //获得证明文件路径
    String getProofPath(@Param("id") String id);
}
