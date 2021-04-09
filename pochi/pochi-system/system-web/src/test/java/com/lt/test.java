package com.lt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class test {
    public static void main(String[] args) throws IOException {
        test1();
        //test2();
    }

    private static void test2() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\configIp.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.seek(0);
        Random r=new Random();
        for (int i = 1; i <= 50000; i++) {
            // 构造config.txt的内容
            int x = r.nextInt(191) + 10;
            int y = r.nextInt(254) + 2;
            int z = r.nextInt(254) + 2;
            int k = r.nextInt(255) + 1;
            raf.seek(raf.length());
            raf.write((x+"."+y+"."+z+"."+k).getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:" + (x+"."+y+"."+z+"."+k));
        }
        raf.close();
        System.out.println("over");
    }

    public static void test1() throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.seek(0);
        for (int i = 1; i <= 50000; i++) {
            // 构造config.txt的内容
            String row = i+"";
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:" + row);
        }
        raf.close();
        System.out.println("over");
    }


}
