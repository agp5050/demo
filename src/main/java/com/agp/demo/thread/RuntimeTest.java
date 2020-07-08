package com.agp.demo.thread;

import com.agp.demo.annotation.TestIntension;
import com.agp.demo.annotation.WrongInvoke;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;

@Slf4j
public class RuntimeTest {
    public static void main(String[] args) {
        Thread tHread;
        Runtime runtime;
        Runtime.getRuntime().exit(1);

    }
    @Test
    public void  runTest(){
        log.info("before runtime.exit");
        Runtime.getRuntime().exit(1);
        log.info("after runtime.exit");
        System.exit(0); // Runtime.getRuntime().exit(status);  等价
    }
    @TestIntension("测试Runtime exec执行命令，调用native方法新建一个进程。并将错误和正确的信息返回")
    @Test
    public void testRuntimeExec() throws IOException {
        Process exec = Runtime.getRuntime().exec("python D:\\jf\\demo\\src\\main\\resources\\testpy.py");
        getExecOutput(exec);
        return;
    }

    private void getExecOutput(Process exec) throws IOException {
        InputStream inputStream = exec.getInputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        bufferedReader.close();
        inputStream.close();
    }

    @Test
    @TestIntension("ProcessBuilder 执行命令启动进程，并且将error output也合并到std output")
    @WrongInvoke("ProcessBuilder(cmd...)，里面的python和后面的文件要分开写。")
    public void testProcessBuilder() throws IOException {
        ProcessBuilder processBuilder=new ProcessBuilder("python","D:\\jf\\demo\\src\\main\\resources\\testpy.py");
        processBuilder.redirectErrorStream(Boolean.TRUE);
        processBuilder.directory(null);
        Process start = processBuilder.start();
        getExecOutput(start);
    }

}
