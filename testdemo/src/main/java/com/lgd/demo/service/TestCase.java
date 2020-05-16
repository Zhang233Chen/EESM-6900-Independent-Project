package com.lgd.demo.service;

import com.lgd.demo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * test case class
 */
@Component
public class TestCase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * testcase1
     * Test single-threaded unoptimized database queries and optimized queries
     */
    public void testCase1(FileWriter fileWriter) throws IOException {
        fileWriter.write("-------------*************************-------------------- \n");
        fileWriter.write("testcase1: \n");
        fileWriter.write("  Test single-threaded database query without optimization and optimized query (multi-table association query) \n");
        long sql_star = System.currentTimeMillis();
        String sql = "SELECT count(1) FROM student s , transcript t where s.id = t.stu_id and t.english >= 60 and t.english <80";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        long sql_end = System.currentTimeMillis();
        System.out.println("The database query time is：" + (sql_end-sql_star)+"ms" );//941
        fileWriter.write("      The database query time is：" + (sql_end-sql_star)+"ms" + "\n");

        long index_star = System.currentTimeMillis();
        String index_sql = "SELECT count(1) FROM student_1 s , transcript t where s.id = t.stu_id and t.english >= 60 and t.english <80";
        List<Map<String, Object>> mapList1 = jdbcTemplate.queryForList(index_sql);
        long index_end = System.currentTimeMillis();
        System.out.println("The optimized query time is：" + (index_end-index_star) +"ms");//677
        fileWriter.write("      The optimized query time is：" + (index_end-index_star) +"ms" + "\n");
        if ((index_end-index_star)<(sql_end-sql_star)) {
            fileWriter.write("Conclusion: Due to the increased index," +
                    " the database query time after optimization is much less than before the index is not added \n");
        }

        fileWriter.write("-------------*************************-------------------- \n");
    }

    /**
     * MySQL and Redis query comparison in single-threaded
     */
    public void testCase2(FileWriter fileWriter) throws IOException {
        fileWriter.write("-------------*************************-------------------- \n");
        fileWriter.write("testcase2: \n");
        fileWriter.write("  compare MySQL and Redis query comparison in single-threaded \n");
        long sql_star = System.currentTimeMillis();
        String sql = "SELECT s.*,t.* FROM student_1 s , transcript t where s.id = t.stu_id";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        long sql_end = System.currentTimeMillis();
        System.out.println("The mysql database query time is：" + (sql_end-sql_star));//1054
        fileWriter.write("      The mysql database query time is：" + (sql_end-sql_star)+"ms" + "\n");

        long redis_star = System.currentTimeMillis();
        Object maplist = redisUtil.getListAll("student_list");
        long redis_end = System.currentTimeMillis();
        System.out.println("The redis database query time is：" + (redis_end-redis_star)); //1319
        fileWriter.write("      The redis database query time is：" + (redis_end-redis_star)+"ms" + "\n");

        fileWriter.write("Conclusion: Under a single thread, the efficiency of the two is similar when querying a single table \n");

        fileWriter.write("-------------*************************-------------------- \n");
    }

    /**
     * MySQL and Redis query comparison in multi-thread
     * @param fileWriter
     * @param threadNum
     * @throws IOException
     */
    public void testCase3(FileWriter fileWriter,int threadNum) throws IOException{
        fileWriter.write("-------------*************************-------------------- \n");
        fileWriter.write("testcase3: \n");
        fileWriter.write("  the performance of multi-thread, MySQL and Redis query comparison (线程数"+threadNum+")\n");
        //create 5 thread pool
        ExecutorService fixPool = Executors.newFixedThreadPool(threadNum);

        CountDownLatch latch = new CountDownLatch(20);
        long sql_star = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    String sql = "SELECT * FROM student_1";
                    jdbcTemplate.queryForList(sql);
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long sql_end = System.currentTimeMillis();
        System.out.println("The mysql database query time is：" + (sql_end-sql_star));
        fileWriter.write("      The mysql database query time is：" + (sql_end-sql_star)+"ms" + "\n");

        CountDownLatch latch2 = new CountDownLatch(20);
        long redis_star = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    redisUtil.getListAll("student_list");
                    latch2.countDown();
                }
            });
        }
        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long redis_end = System.currentTimeMillis();
        System.out.println("The redis database query time is：" + (redis_end-redis_star));
        fileWriter.write("      The redis database query time is：" + (redis_end-redis_star)+"ms" + "\n");
        if ((redis_end-redis_star)<(sql_end-sql_star)) {
            fileWriter.write("Conclusion: in multi-thread we get better performance of redis  \n");
        }
        fileWriter.write("-------------*************************-------------------- \n");

        fixPool.shutdown();
    }
}
