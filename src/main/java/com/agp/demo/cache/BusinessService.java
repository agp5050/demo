package com.agp.demo.cache;

import com.agp.demo.Person;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
public class BusinessService {
    //模擬 數據庫操做
    private static Map map=new ConcurrentHashMap(8);

    @CachePut(value = "agp",key = "#person.id")
    public Person save(Person person){
        log.info("caffeine save :{}={}",person.getId(),person);
        map.put(person.getId(),person);
        return person;
    }

    @CachePut(value = "agp",key = "person.id")
    public Person updateOrCreate(Person person){
        if (!map.containsKey(person.getId())){
            return save(person);
        }else {
            Person oldPerson = (Person)map.get(person.getId());
            oldPerson.setAge(person.getAge());
            oldPerson.setName(person.getName());
            map.put(person.getId(),oldPerson);
            return oldPerson;
        }
    }

    @Cacheable(value = "agp")
    public Object get(Integer id){
        return getInner(id);
    }

    private Object getInner(Integer id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return map.get(id);
    }

    @CacheEvict(value = "agp",key = "#person.id")
    public void del(Person person){
        if (map.containsKey(person.getId())){
            map.remove(person.getId());
        }
    }
    public String getMap(){
        return JSON.toJSONString(map);
    }

}
