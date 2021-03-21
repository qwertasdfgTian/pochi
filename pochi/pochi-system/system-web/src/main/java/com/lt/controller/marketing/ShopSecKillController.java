package com.lt.controller.marketing;

import com.lt.controller.BaseController;
import com.lt.dto.ShopSeckillDto;
import com.lt.enums.StateEnums;
import com.lt.exception.PochiException;
import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopSeckill;
import com.lt.service.ShopSeckillService;
import com.lt.utils.DateUtils;
import com.lt.utils.ShiroUtils;
import com.lt.vo.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/9 20:58
 * @Version 1.0
 */
@RestController
@RequestMapping("/shopSecKill")
public class ShopSecKillController extends BaseController {

    @Reference
    private ShopSeckillService shopSeckillService;

    /**
     * 添加
     * @param seckillDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    //@HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopSeckillDto seckillDto) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        this.shopSeckillService.save(seckillDto,loginUser);
        return new Result<>("添加成功");
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> delete(@PathVariable Long id) {
        ShopSeckill shopSeckill = this.shopSeckillService.get(id);
        if(shopSeckill.getStatus()== StateEnums.SECKILL_START.getCode()){
            throw new PochiException("活动已经开始无法删除");
        }
        this.shopSeckillService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 活动结束
     * @param id
     * @return
     */
    @RequestMapping(value = "/down/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> down(@PathVariable Long id) {
        ShopSeckill shopSeckill = this.shopSeckillService.get(id);
        if(shopSeckill.getStatus()== StateEnums.SECKILL_START.getCode()){
            throw new PochiException("活动已经开始无法结束");
        }
        this.shopSeckillService.down(id);
        return new Result<>("已经成功结束活动");
    }

    /**
     * 分页查询
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopSeckill> page) {
        page = this.shopSeckillService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<ShopSeckill> get(@PathVariable Long id) {
        ShopSeckill shopSeckill = this.shopSeckillService.get(id);
        return new Result<>(shopSeckill);
    }

    /**
     * 查询所有
     * @return
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getAll() throws ParseException {
        List<ShopSeckill> all = this.shopSeckillService.getAll();
        AllSecKillVo allSecKillVo = new AllSecKillVo();
        allSecKillVo.setAll(all);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 第一个没有开始的秒杀的时间
        Date date = null;
        // 创建时间
        for (ShopSeckill shopSeckill : all) {
            if (DateUtils.newDateTime().compareTo(shopSeckill.getBeginTime()) <= 0){
                date=df.parse(shopSeckill.getBeginTime());
                break;
            }
        }
        if(date==null){
            return new Result<>(allSecKillVo);
        }else{
            // 获取开始时间
            Date now = df.parse(DateUtils.newDateTime());
            long l=date.getTime()-now.getTime();
            System.out.println("一共"+l+"毫秒");
            allSecKillVo.setNextSecKillTime(l);
        }
        return new Result<>(allSecKillVo);
    }

    /**
     * 查询是否是秒杀商品
     * @return
     */
    @RequestMapping(value = "/getSecKill/{id}", method = RequestMethod.GET)
    public Result<?> getSecKill(@PathVariable Long id) throws ParseException {
        ProductSecKillVo secKill = this.shopSeckillService.getSecKill(id);
        return new Result<>(secKill);
    }

}
