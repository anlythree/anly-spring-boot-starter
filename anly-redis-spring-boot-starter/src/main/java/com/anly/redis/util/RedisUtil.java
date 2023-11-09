package com.anly.redis.util;

import com.anly.common.utils.AnlyUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author anlythree
 * @description:
 * @time 2022/4/1115:43
 */
@Slf4j
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间（秒）
     * @return true / false
     */
    public Boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: set expire time error!,key:{},time:{}",key,time);
            return false;
        }
    }

    /**
     * 根据 key 获取过期时间
     * @param key 键
     * @return
     */
    public Long getExpire(String key) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error:getExpire error: key is null");
            return null;
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     * @param key 键
     * @return true / false
     */
    public Boolean hasKey(String key) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error:hasKey error: key is null");
            return null;
        }
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("anly-redis error: hasKey error!,key:{}",key);
            return null;
        }
    }

    /**
     * 删除缓存
     * @SuppressWarnings("unchecked") 忽略类型转换警告
     * @param key 键（一个或者多个）
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
//                传入一个 Collection<String> 集合
                redisTemplate.delete(Lists.list(key));
            }
        }
    }

//    ============================== String ==============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true / false
     */
    public Boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: set key-value error!,key:{}",key);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间（秒），如果 time < 0 则设置无限时间
     * @return true / false
     */
    public Boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: set key-value and expire time error!,key:{},value:{},time:{}",key,value,time);
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 递增大小
     * @return
     */
    public Long incr(String key, long delta) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: incr error: key is null");
            return null;
        }
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于 0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 递减大小
     * @return
     */
    public Long decr(String key, long delta) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: decr error: key is null");
            return null;
        }
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于 0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

//    ============================== Map ==============================

    /**
     * HashGet
     * @param key 键（no null）
     * @param item 项（no null）
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取 key 对应的 map
     * @param key 键（no null）
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 值
     * @return true / false
     */
    public Boolean hmset(String key, Map<Object, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: hmset error: key:{},map:{}",key,map);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 值
     * @param time 时间
     * @return true / false
     */
    public Boolean hmset(String key, Map<Object, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: hmset and set expire time error: key:{},map:{},time:{}",key,map,time);
            return false;
        }
    }

    /**
     * 向一张 Hash表 中放入数据，如不存在则创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true / false
     */
    public Boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: hset error: key:{},item:{},value:{}",key,item,value);
            return false;
        }
    }

    /**
     * 向一张 Hash表 中放入数据，并设置时间，如不存在则创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间（如果原来的 Hash表 设置了时间，这里会覆盖）
     * @return true / false
     */
    public Boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: hset error: key:{},item:{},value:{},time:{}",key,item,value,time);
            return false;
        }
    }

    /**
     * 删除 Hash表 中的值
     * @param key 键
     * @param item 项（可以多个，no null）
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断 Hash表 中是否有该键的值
     * @param key 键（no null）
     * @param item 值（no null）
     * @return true / false
     */
    public Boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * Hash递增，如果不存在则创建一个，并把新增的值返回
     * @param key 键
     * @param item 项
     * @param by 递增大小 > 0
     * @return
     */
    public Double hincr(String key, String item, Double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * Hash递减
     * @param key 键
     * @param item 项
     * @param by 递减大小
     * @return
     */
    public Double hdecr(String key, String item, Double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

//    ============================== Set ==============================

    /**
     * 根据 key 获取 set 中的所有值
     * @param key 键
     * @return 值
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("anly-redis error: sGet error: key:{}",key);
            return null;
        }
    }

    /**
     * 从键为 key 的 set 中，根据 value 查询是否存在
     * @param key 键
     * @param value 值
     * @return true / false
     */
    public Boolean sHasKey(String key, Object value) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: sHasKey error: key is null");
            return null;
        }
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("anly-redis error: sHasKey error: key:{},value:{}",key,value);
            return null;
        }
    }

    /**
     * 将数据放入 set缓存
     * @param key 键值
     * @param values 值（可以多个）
     * @return 成功个数
     */
    public Long sSet(String key, Object... values) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: sHasKey error: key is null");
            return null;
        }
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("anly-redis error: sHasKey error: key:{},values:{}",key,values);
        }
        return null;
    }

    /**
     * 将数据放入 set缓存，并设置时间
     * @param key 键
     * @param time 时间
     * @param values 值（可以多个）
     * @return 成功放入个数
     */
    public Long sSet(String key, long time, Object... values) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: sSet error: key is null");
            return null;
        }
        if(AnlyUtil.isArrayEmpty(values)){
            log.error("anly-redis error: sSet error: values is null");
            return null;
        }
        try {
            long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("anly-redis error: sSet error: key:{},time:{},values:{}",key,time,values);
        }
        return null;
    }

    /**
     * 获取 set缓存的长度
     * @param key 键
     * @return 长度
     */
    public Long sGetSetSize(String key) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: sGetSetSize error: key is null");
            return null;
        }
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("anly-redis error: sGetSetSize error: key:{}",key);
            return 0L;
        }
    }

    /**
     * 移除 set缓存中，值为 value 的
     * @param key 键
     * @param values 值
     * @return 成功移除个数
     */
    public Long setRemove(String key, Object... values) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: setRemove error: key is null");
            return null;
        }
        if(AnlyUtil.isArrayEmpty(values)){
            log.error("anly-redis error: setRemove error: values is null");
            return null;
        }
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("anly-redis error: setRemove error: key:{},value:{}",key,values);
            return 0L;
        }
    }

//    ============================== List ==============================

    /**
     * 获取 list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束（0 到 -1 代表所有值）
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("anly-redis error: lGet error: key:{},start:{},end:{}",key,start,end);
            return null;
        }
    }

    /**
     * 获取 list缓存的长度
     * @param key 键
     * @return 长度
     */
    public Long lGetListSize(String key) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: lGetListSize error: key is null");
            return null;
        }
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("anly-redis error: lGetListSize error: key:{}",key);
            return 0L;
        }
    }

    /**
     * 根据索引 index 获取键为 key 的 list 中的元素
     * @param key 键
     * @param index 索引
     *              当 index >= 0 时 {0:表头, 1:第二个元素}
     *              当 index < 0 时 {-1:表尾, -2:倒数第二个元素}
     * @return 值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("anly-redis error: lGetIndex error: key:{},index:{}",key,index);
            return null;
        }
    }

    /**
     * 将值 value 插入键为 key 的 list 中，如果 list 不存在则创建空 list
     * @param key 键
     * @param value 值
     * @return true / false
     */
    public Boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: lSet error: key:{},value:{}",key,value);
            return false;
        }
    }

    /**
     * 将值 value 插入键为 key 的 list 中，并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间
     * @return true / false
     */
    public Boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: lSet error: key:{},value:{},time:{}",key,value,time);
            return false;
        }
    }

    /**
     * 将 values 插入键为 key 的 list 中
     * @param key 键
     * @param values 值
     * @return true / false
     */
    public Boolean lSetList(String key, List<Object> values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: lSetList error: key:{},values:{}",key,values);
            return false;
        }
    }

    /**
     * 将 values 插入键为 key 的 list 中，并设置时间
     * @param key 键
     * @param values 值
     * @param time 时间
     * @return true / false
     */
    public Boolean lSetList(String key, List<Object> values, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: lSetList error: key:{},values:{},time:{}",key,values,time);
            return false;
        }
    }

    /**
     * 根据索引 index 修改键为 key 的值
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return true / false
     */
    public Boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("anly-redis error: lUpdateIndex error: key:{},index:{},value:{}",key,index,value);
            return false;
        }
    }

    /**
     * 在键为 key 的 list 中删除值为 value 的元素
     * @param key 键
     * @param count 如果 count == 0 则删除 list 中所有值为 value 的元素
     *              如果 count > 0 则删除 list 中最左边那个值为 value 的元素
     *              如果 count < 0 则删除 list 中最右边那个值为 value 的元素
     * @param value
     * @return
     */
    public Long lRemove(String key, long count, Object value) {
        if(StringUtils.isEmpty(key)){
            log.error("anly-redis error: lRemove error: key is null");
            return null;
        }
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error("anly-redis error: lRemove error: key:{},count:{},value:{}",key,count,value);
            return 0L;
        }
    }
}
