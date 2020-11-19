package com.agp.demo.thread;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class PipeInputOutputReaderDemo {
    public static void main(String[] args) throws IOException {
        PipedWriter pipedWriter = new PipedWriter();
        PipedReader pipedReader = new PipedReader();
        pipedWriter.connect(pipedReader);
        new Reader(pipedReader).start();
        int recv;
        while (true){
            if ((recv=System.in.read()) ==-1) break;
            pipedWriter.write(recv);
        }
    }
    static class Reader extends Thread{
        PipedReader reader;
        public Reader(PipedReader reader){
            this.reader=reader;
        }
        @Override
        public void run() {
            int n;
            while (true) {
                try {
                    if ((n=reader.read())==-1) break;
                    else {
                        System.out.print((char)n);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
