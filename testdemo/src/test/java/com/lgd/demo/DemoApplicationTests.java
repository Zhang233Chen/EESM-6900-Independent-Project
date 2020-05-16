package com.lgd.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lgd.demo.entity.Student;
import com.lgd.demo.entity.Transcript;
import com.lgd.demo.service.InsertData;
import com.lgd.demo.service.TestCase;
import com.lgd.demo.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private InsertData insertData;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TestCase testCase;

    @Test
    public void test(){
        File file = new File("D:\\result.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);

            testCase.testCase1(fileWriter);//Execute the first case

            fileWriter.write("\n");

            testCase.testCase2(fileWriter);//Execute the second case

            fileWriter.write("\n");

            testCase.testCase3(fileWriter,10);//Execute the third case

            fileWriter.flush();

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //insert 100000 data into the database
    @Test
    public void testInsertData(){
        int num = 100000;
        insertData.inserData(num);
        //insertData.insert2(num);
    }

    @Test
    public void testDeleteData(){
        insertData.deleteData();
    }

    @Test
    public void testRedis(){
        /*List<String> list = new ArrayList<>();
        for(int i=0;i<4;i++){
            Student student = new Student();
            student.setId(i+"");
            student.setAge(16+"");
            student.setName("John"+i);
            student.setSex("0");
            list.add(JSON.toJSONString(student));
        }
        redisUtil.delKey("list");

        redisUtil.setStr("list",list,null);

        Object usermap = redisUtil.getMapValue("usermap", "2");
        System.out.println(usermap);*/
        Object maps = redisUtil.getMap("student_map");
        System.out.println(maps);
    }

    @Test
    public void contextLoads() {
        String property = System.getProperty("user.dir");
        System.out.println(property);

    }

}
