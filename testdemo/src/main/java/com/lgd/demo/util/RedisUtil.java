package com.lgd.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Jedis jedis ;

    public void setStr(String key, String value) {
        setStr(key, value, null);
    }


    public void setStr(String key, Object value, Long time) {
        if(value == null){
            return;
        }
        if(value instanceof String){
            String obj = (String) value;
            stringRedisTemplate.opsForValue().set(key, obj);
        }else if(value instanceof List){
            List obj = (List) value;
            stringRedisTemplate.opsForList().rightPushAll(key,obj);
        }else if(value instanceof Map){
            Map obj = (Map) value;
            stringRedisTemplate.opsForHash().putAll(key,obj);
        }
        if (time != null)
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public Object getKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Object getListKey(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public Object getListKey(String key,long index) {
        return stringRedisTemplate.opsForList().index(key,index);
    }

    public Object getListAll(String key) {
        return stringRedisTemplate.opsForList().range(key,0,-1);
    }

    public Object getListKey(String key,long star,long end) {
        return stringRedisTemplate.opsForList().range(key,star,end);
    }

    public long getListSizeKey(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    public Object getMapValue(String key,String name) {
        return stringRedisTemplate.opsForHash().get(key,name);
    }

    public Object deleteMapKey(String key,String name) {
        return stringRedisTemplate.opsForHash().delete(key,name);
    }

    public Object getMapKeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取所有的map
     * @param key 主键
     * @return
     */
    public Object getMap(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void delKey(String key) {
        stringRedisTemplate.delete(key);
    }


    /**set Object*/
    public String set(String key,Object object)
    {
        return jedis.set(key.getBytes(), SerializeUtil.serialize(object));
    }

    /**get Object*/
    public Object get(String key)
    {
        byte[] value = jedis.get(key.getBytes());
        return SerializeUtil.unserizlize(value);
    }

    /**delete a key**/
    public boolean del(String key)
    {
        return jedis.del(key.getBytes())>0;
    }


}
