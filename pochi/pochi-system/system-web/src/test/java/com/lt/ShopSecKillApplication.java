package com.lt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lt.dto.ShopUserLoginDto;
import com.lt.enums.StateEnums;
import com.lt.mapper.ShopUserMapper;
import com.lt.pojo.ShopUser;
import com.lt.utils.IdWorker;
import com.lt.vo.Result;
import com.lt.vo.TokenVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.ws.Response;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootTest
public class ShopSecKillApplication {

    @Autowired
    private ShopUserMapper shopUserMapper;
    @Autowired
    private IdWorker idWorker;

    // 创建用户
    @Test
    public void createUser(){
        ShopUser shopUser = new ShopUser();
        for (int i = 1; i <= 50000; i++) {
            Long id = idWorker.nextId();
            shopUser.setId(id);
            shopUser.setPhone(i+"");
            shopUser.setOpenId(i+"");
            shopUser.setPassword(i+"");
            shopUser.setStatus(StateEnums.ENABLED.getCode());
            shopUser.setGender(StateEnums.SEX_MAN.getCode());
            shopUser.setPoint(new BigDecimal(0));
            shopUser.setDeleted(StateEnums.NOT_DELETED.getCode());
            this.shopUserMapper.insert(shopUser);
        }
    }

    // 登录用户拿到token写入文件
    @Test
    public void loginandcreatetoken() throws IOException {
        String urlString = "http://localhost:8080/wx/wxLoginByPhone";
        File file = new File("C:\\Users\\Administrator\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.seek(0);
        for (int i = 1; i <= 50000; i++) {
            ShopUserLoginDto shopUserLoginDto = new ShopUserLoginDto();
            shopUserLoginDto.setPhone(i+"");
            shopUserLoginDto.setPassword(i+"");
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            co.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            OutputStream out = co.getOutputStream();
            String params = JSON.toJSONString(shopUserLoginDto);
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            // 拿到返回的值
            String reponse = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            Result result = mapper.readValue(reponse, Result.class);
            // 把值转换成json后取出
            String tokenVo = JSON.toJSONString(result.getData().toString()).substring(8,JSON.toJSONString(result.getData().toString()).length()-2);
            System.out.println("create user token:"+ tokenVo);
            // 构造config.txt的内容
            String row = tokenVo;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:" + tokenVo);
        }
        raf.close();
        System.out.println("over");
    }

    // 创建用户
    @Test
    public void createUserId() throws IOException {
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
