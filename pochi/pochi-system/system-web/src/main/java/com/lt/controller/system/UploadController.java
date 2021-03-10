package com.lt.controller.system;

import com.lt.enums.ResultEnums;
import com.lt.exception.PochiException;
import com.lt.config.upload.UploadService;
import com.lt.controller.BaseController;
import com.lt.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 文件上传控制器
 * @Author Mr.Tian
 */
@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {

    @Autowired
    private UploadService uploadService;

    /**
     * 文件图片 ---检查结果
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Result<?> uploadFile(MultipartFile mf){
        Map<String,Object> map=new HashMap<>();
        if(null!=mf){
            map.put("name",mf.getOriginalFilename());
            String path = this.uploadService.uploadImage(mf);
            map.put("url",path);
            System.out.println(map);
            return new Result<>("文件上传成功",map.get("url"));
        }else{
            throw new PochiException(ResultEnums.FILE_UPLOAD_ERROR);
        }
    }
}
