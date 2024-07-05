/*
 * Copyright 2019-2028 Beijing Daotiandi Technology Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Author: xuzhanfu (7333791@qq.com)
 */
package com.anly.redis.core;

import com.alibaba.fastjson2.JSONObject;
import com.anly.common.api.ResultCode;
import com.anly.common.exception.AnlyException;
import com.anly.common.utils.AnlyCollectionUtil;
import com.anly.redis.util.RedisLockUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 定义常用的 Redis操作
 *
 * @author anlythree
 * @date 2019-10-11 19:02
 **/
@Slf4j
public class RedisService {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private RedisLockUtil redisLockUtil;

	/**
	 * 指定缓存失效时间
	 *
	 * @param key  键
	 * @param time 时间(秒)
	 * @return Boolean
	 */
	public Boolean expire(String key, Long time) {
		try {
			if (time > 0) {
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: set expire time error!,key:{},time:{}",key,time);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 根据key获取过期时间
	 *
	 * @param key 键 不能为 null
	 * @return 时间(秒) 返回 0代表为永久有效
	 */
	public Long getExpire(String key) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error:getExpire error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 添加到带有 过期时间的  缓存
	 *
	 * @param key      redis主键
	 * @param value    值
	 * @param time     过期时间 单位毫秒
	 */
	public void setWithExpire(final String key, final Object value, final Long time) {
		setWithExpire(key,value,time,TimeUnit.MILLISECONDS);
	}

	/**
	 * 添加到带有 过期时间的  缓存
	 *
	 * @param key      redis主键
	 * @param value    值
	 * @param time     过期时间
	 * @param timeUnit 过期时间单位
	 */
	public void setWithExpire(final String key, final Object value, final long time, final TimeUnit timeUnit) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, timeUnit);
			} else {
				set(key, value);
			}
		} catch (Exception e) {
			log.error("anly-redis error: set key-value and expire time error!,key:{},value:{},time:{}",key,value,time);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	public void setExpire(final String key, final Object value, final long time, final TimeUnit timeUnit, RedisSerializer<Object> valueSerializer) {
		byte[] rawKey = rawKey(key);
		byte[] rawValue = rawValue(value, valueSerializer);

		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				potentiallyUsePsetEx(connection);
				return null;
			}

			public void potentiallyUsePsetEx(RedisConnection connection) {
				if (!TimeUnit.MILLISECONDS.equals(timeUnit) || !failsafeInvokePsetEx(connection)) {
					connection.setEx(rawKey, TimeoutUtils.toSeconds(time, timeUnit), rawValue);
				}
			}

			private boolean failsafeInvokePsetEx(RedisConnection connection) {
				boolean failed = false;
				try {
					connection.pSetEx(rawKey, time, rawValue);
				} catch (UnsupportedOperationException e) {
					failed = true;
				}
				return !failed;
			}
		}, true);
	}

	/**
	 * 判断 key是否存在
	 *
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public Boolean hasKey(String key) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error:hasKey error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			log.error("anly-redis error: hasKey error!,key:{}",key);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 删除缓存
	 *
	 * @param key 可以传一个值 或多个
	 */
	public void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				redisTemplate.delete(key[0]);
			} else {
				redisTemplate.delete(Arrays.asList(key));
			}
		}
	}

	/**
	 * 普通缓存获取
	 *
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key) {
		return key == null ? null : redisTemplate.opsForValue().get(key);
	}

	/**
	 * 普通缓存根据提供类获取
	 *
	 * @param key 键
	 * @return 值
	 */
	public <T> T get(String key,Class<T> resClass) {
		if(StringUtils.isEmpty(key)){
			return null;
		}
		Object o = redisTemplate.opsForValue().get(key);
		if(o == null){
			return null;
		}
		return resClass.cast(o);
	}

	/**
	 * 根据key获取对象
	 *
	 * @param key             the key
	 * @param valueSerializer 序列化
	 * @return the string
	 */
	public Object get(final String key, RedisSerializer<Object> valueSerializer) {
		byte[] rawKey = rawKey(key);
		return redisTemplate.execute(connection -> deserializeValue(connection.get(rawKey), valueSerializer), true);
	}

	/**
	 * 普通缓存放入
	 *
	 * @param key   键
	 * @param value 值
	 * @return true成功 false失败
	 */
	public Boolean set(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: set key-value error!,key:{}",key);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(大于0)
	 * @return Long
	 */
	public Long incr(String key, Long delta) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: incr error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		if (delta < 0) {
			throw new AnlyException("递增因子必须大于 0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(小于0)
	 * @return Long
	 */
	public Long decr(String key, Long delta) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: decr error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于 0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * HashGet
	 *
	 * @param key  键 不能为 null
	 * @param item 项 不能为 null
	 * @return 值
	 */
	public Object hGet(String key, String item) {
		return redisTemplate.opsForHash().get(key, item);
	}

	/**
	 * Hash Get 指定返回值类型
	 *
	 * @param key  键 不能为 null
	 * @param item 项 不能为 null
	 * @return 值
	 */
	public <T> T hGet(String key, String item, Class<T> resClass) {
		Object o = redisTemplate.opsForHash().get(key, item);
		if(o == null){
			return null;
		}
		return resClass.cast(o);
	}

	/**
	 * 获取hash中的一个value
	 *
	 * @param key  键 不能为 null
	 * @param item 项 不能为 null
	 * @return 值
	 */
	public String hGetString(String key, String item) {
		HashOperations<String, String, String> hashOperations =
				redisTemplate.opsForHash();
		return hashOperations.get(key, item);
	}

	/**
	 * 获取 hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Map<String, Object> hGetAll(String key) {
		HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
		return hashOperations.entries(key);
	}

	/**
	 * 获取 hashKey对应的所有键值,指定返回值类型
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public <T> Map<String, T> hGetAll(String key, Class<T> resClass) {
		HashOperations<String, String, T> hashOperations = redisTemplate.opsForHash();
		return hashOperations.entries(key);
	}

	/**
	 * 获取 hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Map<String, String> hGetAllString(String key) {
		HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		return hashOperations.entries(key);
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public Boolean hSetAll(String key, Map<String, ?> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: hSetAll error: key:{},map:{}",key,map);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * HashSet 并设置时间
	 *
	 * @param key  键
	 * @param map  对应多个键值
	 * @param time 时间(秒)
	 * @return true成功 false失败
	 */
	public Boolean hSetAll(String key, Map<String, ?> map, Long time) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: hSetAll and set expire time error: key:{},map:{},time:{}",key,map,time);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true 成功 false失败
	 */
	public Boolean hSet(String key, String item, Object value) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: hSet error: key:{},item:{},value:{}",key,item,value);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public Boolean hSet(String key, String item, Object value, Long time) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: hSet error: key:{},item:{},value:{},time:{}",key,item,value,time);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 删除hash表中的值
	 *
	 * @param key  键 不能为 null
	 * @param item 项 可以使多个不能为 null
	 */
	public void hDel(String key, Object... item) {
		redisTemplate.opsForHash().delete(key, item);
	}

	/**
	 * 判断hash表中是否有该项的值
	 *
	 * @param key  键 不能为 null
	 * @param item 项 不能为 null
	 * @return true 存在 false不存在
	 */
	public Boolean hHasKey(String key, String item) {
		return redisTemplate.opsForHash().hasKey(key, item);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param by   要增加几(大于0)
	 * @return Double
	 */
	public Double hIncr(String key, String item, Double by) {
		return redisTemplate.opsForHash().increment(key, item, by);
	}

	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param by   要减少记(小于0)
	 * @return Double
	 */
	public Double hDecr(String key, String item, Double by) {
		return redisTemplate.opsForHash().increment(key, item, -by);
	}

	/**
	 * 根据 key获取 Set中的所有值
	 *
	 * @param key 键
	 * @return Set
	 */
	public Set<Object> sGet(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			log.error("anly-redis error: sGet error: key:{}",key);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public Boolean sHasKey(String key, Object value) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: sHasKey error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			log.error("anly-redis error: sHasKey error: key:{},value:{}",key,value);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 将数据放入set缓存
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Long sSet(String key, Object... values) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: sHasKey error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			log.error("anly-redis error: sHasKey error: key:{},values:{}",key,values);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 将set数据放入缓存
	 *
	 * @param key    键
	 * @param time   时间(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Long sSetWithExpireTime(String key, Long time, Object... values) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: sSet error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		if(AnlyCollectionUtil.isArrayEmpty(values)){
			log.error("anly-redis error: sSet error: values is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			long count = redisTemplate.opsForSet().add(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return count;
		} catch (Exception e) {
			log.error("anly-redis error: sSet error: key:{},time:{},values:{}",key,time,values);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 获取set缓存的长度
	 *
	 * @param key 键
	 * @return Long
	 */
	public Long sGetSetSize(String key) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: sGetSetSize error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			log.error("anly-redis error: sGetSetSize error: key:{}",key);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 移除值为value的
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数
	 */
	public Long setRemove(String key, Object... values) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: setRemove error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		if(AnlyCollectionUtil.isArrayEmpty(values)){
			log.error("anly-redis error: setRemove error: values is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.opsForSet().remove(key, values);
		} catch (Exception e) {
			log.error("anly-redis error: setRemove error: key:{},value:{}",key,values);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return List
	 */
	public List<Object> lGet(String key, Long start, Long end) {
		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			log.error("anly-redis error: lGet error: key:{},start:{},end:{}",key,start,end);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 获取list缓存的长度
	 *
	 * @param key 键
	 * @return Long
	 */
	public Long lGetListSize(String key) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: lGetListSize error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			log.error("anly-redis error: lGetListSize error: key:{}",key);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 通过索引 获取list中的值
	 *
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；
	 *              index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return Object
	 */
	public Object lGetIndex(String key, Long index) {
		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			log.error("anly-redis error: lGetIndex error: key:{},index:{}",key,index);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return Boolean
	 */
	public Boolean lSet(String key, Object value) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: lSet error: key:{},value:{}",key,value);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return Boolean
	 */
	public Boolean lSet(String key, Object value, Long time) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: lSet error: key:{},value:{},time:{}",key,value,time);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return Boolean
	 */
	public Boolean lSet(String key, List<Object> value) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: lSetList error: key:{},values:{}",key,value);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return Boolean
	 */
	public Boolean lSet(String key, List<Object> value, Long time) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: lSetList error: key:{},values:{},time:{}",key,value,time);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 根据索引修改list中的某条数据
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return Boolean
	 */
	public Boolean lUpdateIndex(String key, Long index, Object value) {
		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			log.error("anly-redis error: lUpdateIndex error: key:{},index:{},value:{}",key,index,value);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数
	 */
	public Long lRemove(String key, Long count, Object value) {
		if(StringUtils.isEmpty(key)){
			log.error("anly-redis error: lRemove error: key is null");
			throw new AnlyException(ResultCode.ERROR);
		}
		try {
			return redisTemplate.opsForList().remove(key, count, value);
		} catch (Exception e) {
			log.error("anly-redis error: lRemove error: key:{},count:{},value:{}",key,count,value);
			throw new AnlyException(ResultCode.ERROR);
		}
	}

	/**
	 * redis List数据结构 : 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 end 指定。
	 *
	 * @param key             the key
	 * @param start           the start
	 * @param end             the end
	 * @param valueSerializer 序列化
	 * @return the list
	 */
	public List<Object> getList(String key, int start, int end, RedisSerializer<Object> valueSerializer) {
		byte[] rawKey = rawKey(key);
		return redisTemplate.execute(connection -> deserializeValues(connection.lRange(rawKey, start, end), valueSerializer), true);
	}

	private byte[] rawKey(Object key) {
		Assert.notNull(key, "non null key required");

		if (key instanceof byte[]) {
			return (byte[]) key;
		}
		RedisSerializer<Object> redisSerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
		return redisSerializer.serialize(key);
	}

	private byte[] rawValue(Object value, RedisSerializer<Object> valueSerializer) {
		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		return valueSerializer.serialize(value);
	}

	private List<Object> deserializeValues(List<byte[]> rawValues, RedisSerializer<Object> valueSerializer) {
		if (valueSerializer == null) {
			return rawValues.stream().map(Object::toString).collect(Collectors.toList());
		}
		return SerializationUtils.deserialize(rawValues, valueSerializer);
	}

	private Object deserializeValue(byte[] value, RedisSerializer<Object> valueSerializer) {
		if (valueSerializer == null) {
			return value;
		}
		return valueSerializer.deserialize(value);
	}


	/**
	 * 分布式锁
	 *
	 * @param key        分布式锁key
	 * @param expireTime 持有锁的最长时间 (redis过期时间) 秒为单位
	 * @return 返回获取锁状态 成功失败
	 */
	public boolean tryLock(String key, int expireTime) {
		return redisLockUtil.tryLock(key, expireTime);
	}

	public void unLock(String key) {
		redisLockUtil.releaseLock(key);
	}

}
