package com.grgbanking.grgacd.common;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wjqiu
 * @date 2019-04-21
 */

public class RedisUtil<T> {

    private BoundListOperations<String, T> listOperations;//noblocking
    private BoundSetOperations<String, T> setOperations;
    private BoundValueOperations<String, T> valueOperations;
    private BoundHashOperations<String, String , T> hashOperations;
    private BoundZSetOperations zSetOperations;

    private static Lock lock = new ReentrantLock();//基于底层IO阻塞考虑

    private RedisTemplate redisTemplate;

    private byte[] rawKey;
    private String key;
    public RedisUtil(RedisTemplate redisTemplate, String key){
        this.redisTemplate = redisTemplate;
        this.key = key;
        rawKey = redisTemplate.getKeySerializer().serialize(key);
        listOperations = redisTemplate.boundListOps(key);
        setOperations = redisTemplate.boundSetOps(key);
        valueOperations = redisTemplate.boundValueOps(key);
        hashOperations = redisTemplate.boundHashOps(key);
        zSetOperations = redisTemplate.boundZSetOps(key);
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * listRemove and get last item from queue:BRPOP
     * @return
     */
    public T takeFromTail(int timeout) throws InterruptedException{
//        lock.lockInterruptibly();
//        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
//        RedisConnection connection = connectionFactory.getConnection();
//        try{
//            List<byte[]> results = connection.bRPop(timeout, rawKey);
//            if(CollectionUtils.isEmpty(results)){
//                return null;
//            }
//            return (T)redisTemplate.getValueSerializer().deserialize(results.get(1));
//        }finally{
//            lock.unlock();
//            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
//        }
        return listOperations.rightPop(timeout,TimeUnit.SECONDS);
    }

    public T takeFromTail() throws InterruptedException{
        return takeFromTail(0);
    }

    /**
     * 从队列的头，插入
     */
    public void pushFromHead(T value){
        listOperations.leftPush(value);
    }

    public void pushFromTail(T value){
        listOperations.rightPush(value);
    }

    /**
     * noblocking
     * @return null if no item in queue
     */
    public T removeFromHead(){
        return listOperations.leftPop();
    }

    public T removeFromTail(){
        return listOperations.rightPop();
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * listRemove and get first item from queue:BLPOP
     * @return
     */
    public T takeFromHead(int timeout){
//        try {
//            lock.lockInterruptibly();
//
//            RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
//            RedisConnection connection = connectionFactory.getConnection();
//            try{
//                List<byte[]> results = connection.bLPop(timeout, rawKey);
//                if(CollectionUtils.isEmpty(results)){
//                    return null;
//                }
//                return (T)redisTemplate.getValueSerializer().deserialize(results.get(1));
//            }finally{
//                lock.unlock();
//                RedisConnectionUtils.releaseConnection(connection, connectionFactory);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
        return listOperations.leftPop(timeout,TimeUnit.SECONDS);
    }

    public T takeFromHead(){
        return takeFromHead(10);
    }

    public Long listRemove(T value){
        return listOperations.remove(0,value);
    }

    public List<T> listGetAll(){
        return listOperations.range(0,-1);
    }

    public Long getListSize(){
        return listOperations.size();
    }


    public void listAdd(long index,T value){
        listOperations.set(index,value);
    }


    /**
     * Value
     */
    public boolean exists() {
        return redisTemplate.hasKey(this.key);
    }

    public Object get() {
        return valueOperations.get();
    }

    public void set(T value) {
        valueOperations.set(value);
    }
    public void set(T value, Long expireTime) {
        valueOperations.set(value, expireTime, TimeUnit.SECONDS);
    }
    public boolean setExpireTime(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * Hash Map
     */
    public Long getMapSize() {
        return hashOperations.size();
    }
    public Map<String, T> getMap() {
        return hashOperations.entries();
    }
    public T getMapField(String field) {
        return (T) hashOperations.get(field);
    }
    public Boolean hasMapKey(String field) {
        return hashOperations.hasKey(field);
    }


    public List<T> getMapFieldValues() {
        return hashOperations.values();
    }


    public Set<String> getMapFieldKeys() {
        return hashOperations.keys();
    }


    public void addMap(Map<String, T> map) {
        hashOperations.putAll(map);
    }


    public void addMap(String field, T value) {
        hashOperations.put(field, value);
    }


    public void addMap(String field, T value, long time) {
        hashOperations.put(field, value);
        hashOperations.expire(time, TimeUnit.SECONDS);
    }
    public void removeMapField(Object... field) {
        hashOperations.delete(field);
    }

    /**
     * Set
     */
    public void addSet(T... obj) {
        setOperations.add(obj);
    }


    public long removeSetValue(T obj) {
        return setOperations.remove(obj);
    }


    public long removeSetValue(T... obj) {
        if (obj != null && obj.length > 0) {
            return setOperations.remove(obj);
        }
        return 0L;
    }


    public Long  getSetSize() {
        return setOperations.size();
    }


    public Boolean hasSetValue(T obj) {
        return setOperations.isMember(obj);
    }


    public Set<T> getSet() {
        return setOperations.members();
    }

    public void removeAll() {
        redisTemplate.delete(this.key);
    }

    /*zset数据类型操作方法--start*/

    /**
     * 添加一个元素及其分数
     * @param o 元素
     * @param score 分数
     * @return
     * @author hsjiang
     * @date 2019/7/19/019
     **/
    public boolean zSetAddOne(T o,double score){
        return zSetOperations.add(o,score);
    }

    /**
     * 修改元素的分数
     * @param o 元素
     * @param score 分数
     * @return
     * @author hsjiang
     * @date 2019/7/19/019
     **/
    public void zSetIncrementScore(T o, double score){
        if(zSetOperations.rank(o) == null){
            zSetAddOne(o,score);
        }
        else{
            zSetOperations.incrementScore(o,score);
        }

    }
    /**
     * 根据score，获取元素排名，从小到大
     * @param
     * @return
     * @author hsjiang
     * @date 2019/7/19/019
     **/
    public Set<T> zSetRange(){
        return zSetOperations.range(0,-1);
    }

    /**
     * 获取分数最低的元素
     * @param
     * @return
     * @author hsjiang
     * @date 2019/7/22/022
     **/
    public T zSetGetLowest(){
        T result = null;
        Set<T> set = zSetOperations.range(0,1);
        if(set != null){
            Iterator<T> iterator = set.iterator();
            if (iterator.hasNext()) {
                result = iterator.next();
            }
        }

        return result;
    }

    /**
     * 检查缓存是否包含某元素
     * @param t 元素
     * @return
     * @author hsjiang
     * @date 2019/7/22/022
     **/
    public boolean zSetContain(T t){
        boolean result = false;
        Long rank = zSetOperations.rank(t);
        if( rank != null){
            result = true;
        }
        return result;
    }

    /**
     * 删除元素
     * @param t 需要删除的元素
     * @return
     * @author hsjiang
     * @date 2019/7/22/022
     **/
    public Long zSetRemove(T ...t){
        return zSetOperations.remove(t);
    }
}
