package com.agp.demo.serialization;

import com.agp.demo.Person;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KryoTest {
    public static void main(String[] args) throws IOException {
        Kryo kryo=new Kryo();
        kryo.register(Person.class);
        Person person=new Person();
        person.setId(1);
        person.setName("age");
        person.setAge(23);
        Output out=new Output(new FileOutputStream("person.kryo")) ;
        File file = Paths.get("person.kryo").toFile();
        System.out.println(file.getAbsolutePath());
        System.out.println(file.length());
        System.out.println(file.getFreeSpace());
        System.out.println(file.getTotalSpace());
        kryo.writeObject(out,person);
        out.close();
        Input input=new Input(new FileInputStream("person.kryo"));
        Person person1 = kryo.readObject(input, Person.class);
        System.out.println(person1);


    }
}
