package com.agp.demo.nio;

import com.agp.demo.Person;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IOTest {
    @Test
    public void testObjectInOutPutStream() throws IOException, ClassNotFoundException {
        String fileName="D:\\study\\demo\\src\\main\\resources\\abc.txt";
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
        objectOutputStream.writeInt(123);
        objectOutputStream.writeChars("ab");
        objectOutputStream.writeObject(new Person(12,"xx",23));
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
        int firstInt = objectInputStream.readInt();
        Object secondString = objectInputStream.readChar();
        Object second2String = objectInputStream.readChar();
        Object thirdPerson = objectInputStream.readObject();
        System.out.println(firstInt);
        System.out.println(secondString);
        System.out.println(second2String);
        System.out.println(thirdPerson);

    }
}
