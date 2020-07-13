package com.agp.demo.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NIOTest {
   public void testCopy() throws IOException {
       String sPath="";
       String dPath="";
       Files.copy(Paths.get(sPath),Paths.get(dPath));
   }
}
