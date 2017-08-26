package edu.cs.scu.dao.impl;

import com.alibaba.fastjson.JSON;
import edu.cs.scu.bean.UserBean;
import edu.cs.scu.common.constants.TableConstants;
import edu.cs.scu.conf.JedisPoolManager;
import edu.cs.scu.dao.BaseDao;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息数据库接口
 * <p>
 * Created by Wang Han on 2017/6/19 13:58.
 * E-mail address is wanghan0501@vip.qq.com.
 * Copyright © 2017 Wang Han. SCU. All Rights Reserved.
 */
public class UserDaoImpl extends BaseDao {

    @Override
    public void add(List<Object> objectList) {
        ShardedJedis jedis = JedisPoolManager.getResource();
        Map<String, String> userTableMap = new HashMap<>();
        for (Object o : objectList) {
            UserBean userBean = (UserBean) o;
            String Key = userBean.getMac() + "||" + String.valueOf(userBean.getShopId());
            String value = JSON.toJSONString(userBean);
            userTableMap.put(Key, value);
        }
        jedis.hmset(TableConstants.TABLE_USER, userTableMap);
        jedis.close();
    }


    @Override
    public Object get(String key) {
        ShardedJedis jedis = JedisPoolManager.getResource();
        List<String> userList = jedis.hmget(TableConstants.TABLE_USER, key);
        UserBean user = JSON.parseObject(userList.get(0), UserBean.class);
        jedis.close();
        return user;
    }

    @Override
    public List<Object> get(List<String> keys) {
        ShardedJedis jedis = JedisPoolManager.getResource();
        List<String> userList = jedis.hmget(TableConstants.TABLE_USER, keys.toArray(new String[0]));
        List<Object> userBeanList = new ArrayList<>();
        for (String user : userList) {
            userBeanList.add(JSON.parseObject(user, UserBean.class));
        }
        jedis.close();
        return userBeanList;
    }
}