package com.agp.demo.cache;

import com.agp.demo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CachePersonController {
    @Autowired
    BusinessService service;
    @GetMapping("/get/{id}")
    public Object get(@PathVariable("id") Integer id){
        return service.get(id);
    }
    @PostMapping("/create")
    public Person create(@RequestBody Person person){
        return service.save(person);
    }
    @PutMapping("/update")
    public Person update(@RequestBody Person person){
        return service.updateOrCreate(person);
    }
    @PutMapping("/del")
    public void del(@RequestBody Person person){
         service.del(person);
    }
    @GetMapping("/getMap")
    public String getDBString(){
        return service.getMap();
    }
}
