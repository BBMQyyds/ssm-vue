package com.jdsbbmq;

import com.jdsbbmq.dao.CourseMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    @org.junit.Test
    public void test() {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        CourseMapper courseMapper = context.getBean(CourseMapper.class);
    }
}
