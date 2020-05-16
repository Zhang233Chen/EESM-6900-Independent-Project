package com.lgd.demo.service;

import com.alibaba.fastjson.JSON;
import com.lgd.demo.entity.Student;
import com.lgd.demo.entity.Transcript;
import com.lgd.demo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
public class InsertData {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisUtil redisUtil;

    public void inserData(int num){
        List<Object[]> objects = new ArrayList<>();
        List<Student> list = new ArrayList<>();
        List<String> jsonSList = new ArrayList<>();
        List<String> jsonTList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        int zs_count = 1;
        int ls_count = 1;
        for(int i =1;i<=num;i++){
            Student student = new Student();
            student.setId(String.valueOf(i));
            if(i%2==0){
                student.setName("John_"+zs_count);
                student.setAge(String.valueOf(18));
                student.setSex("male");
                zs_count++;
            }else {
                student.setName("Mike_"+ls_count);
                student.setAge(String.valueOf(19));
                student.setSex("female");
                ls_count++;
            }

            list.add(student);
            jsonSList.add(JSON.toJSONString(student));

            int chinese = (int) (Math.random( )*50+50) ;
            int math = (int) (Math.random( )*50+50) ;
            int engilsh = (int) (Math.random( )*50+50) ;
            int total = chinese + math + engilsh;
            Object[] o = new Object[]{i,i,chinese,math,engilsh,total};

            Transcript transcript = new Transcript();
            transcript.setId(i+"");
            transcript.setStu_id(i+"");
            transcript.setChinese(chinese);
            transcript.setMath(math);
            transcript.setEnglish(engilsh);
            transcript.setTotal(total);

            student.setTranscript(transcript);

            objects.add(o);
            jsonTList.add(JSON.toJSONString(transcript));
            map.put(String.valueOf(i), JSON.toJSONString(student));
        }

        //insert date into redis
        redisUtil.setStr("student_list", jsonSList,null);
        System.out.println("student_list");
        redisUtil.setStr("student_map", map,null);
        System.out.println("student_map");
        redisUtil.setStr("transcript_list", jsonTList,null);
        System.out.println("transcript_list inserting completed ");

        //insert data into mysql
        String insertSql = "insert into student (id,name,age,sex) values (?,?,?,?)";
        int[] ints = jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, list.get(i).getId());
                ps.setString(2, list.get(i).getName());
                ps.setString(3, list.get(i).getAge());
                ps.setString(4, list.get(i).getSex());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });

        String insertSql1 = "insert into student_1 (id,name,age,sex) values (?,?,?,?)";
        jdbcTemplate.batchUpdate(insertSql1, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, list.get(i).getId());
                ps.setString(2, list.get(i).getName());
                ps.setString(3, list.get(i).getAge());
                ps.setString(4, list.get(i).getSex());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });

        //insert data into sql
        String inset_sql = "insert into transcript (id,stu_id,chinese,math,english,total) values (?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(inset_sql,objects);

        System.out.println("database have been inserted");


    }

    public void insert2(int num){
        List<Object[]> objects = new ArrayList<>();
        List<String> jsonList = new ArrayList<>();
        for(int i =1;i<=num;i++){
            int chinese = (int) (Math.random( )*50+50) ;
            int math = (int) (Math.random( )*50+50) ;
            int engilsh = (int) (Math.random( )*50+50) ;
            int total = chinese + math + engilsh;
            Object[] o = new Object[]{i,i,chinese,math,engilsh,total};

            Transcript transcript = new Transcript();
            transcript.setId(i+"");
            transcript.setStu_id(i+"");
            transcript.setChinese(chinese);
            transcript.setMath(math);
            transcript.setEnglish(engilsh);
            transcript.setTotal(total);

            objects.add(o);
            jsonList.add(JSON.toJSONString(transcript));
        }

        //redis
        redisUtil.setStr("transcript_list", jsonList,null);
        System.out.println("transcript_list completed");

        //mysql
        String inset_sql = "insert into transcript (id,stu_id,chinese,math,english,total) values (?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(inset_sql,objects);
        System.out.println("database inserting completed");
    }

    public void deleteData(){
        //delete data in mysql
        String sql = "delete from student";
        jdbcTemplate.execute(sql);
        sql = "delete from student_1";
        jdbcTemplate.execute(sql);
        sql = "delete from transcript";
        jdbcTemplate.execute(sql);


        //delete date in redis
        redisUtil.delKey("userlist");
        redisUtil.delKey("student_list");
        redisUtil.delKey("student_map");
        redisUtil.delKey("transcript_list");
    }

}
