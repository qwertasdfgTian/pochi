package com.lt.controller.marketing;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.lt.constant.CoreConstant;
import com.lt.controller.BaseController;
import com.lt.dto.GoShopSeckillDto;
import com.lt.dto.ShopSeckillDto;
import com.lt.enums.StateEnums;
import com.lt.exception.PochiException;
import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopOrder;
import com.lt.pojo.ShopSeckill;
import com.lt.service.ShopOrderService;
import com.lt.service.ShopSeckillService;
import com.lt.utils.DateUtils;
import com.lt.utils.ShiroUtils;
import com.lt.vo.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Reference
    private ShopOrderService shopOrderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ZooKeeper zooKeeper;

    // 引入令牌桶算法的对象
    private RateLimiter rateLimiter = RateLimiter.create(20);

    // jvm缓存标志
    private static ConcurrentHashMap<Long, Boolean> productSoldOutMap = new ConcurrentHashMap<>();

    // 获取jvm缓存标志
    public static ConcurrentHashMap<Long, Boolean> getProductSoldOutMap() {
        return productSoldOutMap;
    }

    /**
     * 添加
     *
     * @param seckillDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    //@HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopSeckillDto seckillDto) throws ParseException {
        List<ShopSeckill> list = this.shopSeckillService.getByProductId(seckillDto.getProductId());
        if (list.size() >= 1) {
            // 说明存在已经参加活动的商品，不能在重复设置活动
            return new Result<>("添加失败，改商品已经有参加的活动");
        }
        if (productSoldOutMap.get(seckillDto.getProductId()) != null) {
            productSoldOutMap.remove(seckillDto.getProductId());
        }
        LoginUser loginUser = ShiroUtils.getLoginUser();
        ShopSeckill shopSeckill = this.shopSeckillService.save(seckillDto, loginUser);
        // 秒杀优化：把商品库存放在redis
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nowtime = df.parse(DateUtils.newDateTime()).getTime();
        long endtime = df.parse(seckillDto.getEndTime()).getTime();
        Long time = endtime - nowtime;
        this.stringRedisTemplate.opsForValue().set(seckillDto.getProductId().toString(), seckillDto.getStock().toString(), time, TimeUnit.MILLISECONDS);
        return new Result<>("添加成功");
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> delete(@PathVariable Long id) {
        ShopSeckill shopSeckill = this.shopSeckillService.get(id);
        if (shopSeckill.getStatus() == StateEnums.SECKILL_START.getCode()) {
            throw new PochiException("活动已经开始无法删除");
        }
        this.shopSeckillService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 活动结束
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/down/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> down(@PathVariable Long id) {
        ShopSeckill shopSeckill = this.shopSeckillService.get(id);
        if (shopSeckill.getStatus() == StateEnums.SECKILL_START.getCode()) {
            throw new PochiException("活动已经开始无法结束");
        }
        this.shopSeckillService.down(id);
        return new Result<>("已经成功结束活动");
    }

    /**
     * 分页查询
     *
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
     *
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
            if (DateUtils.newDateTime().compareTo(shopSeckill.getBeginTime()) <= 0) {
                date = df.parse(shopSeckill.getBeginTime());
                break;
            }
        }
        if (date == null) {
            return new Result<>(allSecKillVo);
        } else {
            // 获取开始时间
            Date now = df.parse(DateUtils.newDateTime());
            long l = date.getTime() - now.getTime();
            System.out.println("一共" + l + "毫秒");
            allSecKillVo.setNextSecKillTime(l);
        }
        return new Result<>(allSecKillVo);
    }

    /**
     * 查询是否是秒杀商品
     *
     * @return
     */
    @RequestMapping(value = "/getSecKill/{id}", method = RequestMethod.GET)
    public Result<?> getSecKill(@PathVariable Long id) throws ParseException {
        ProductSecKillVo secKill = this.shopSeckillService.getSecKill(id);
        return new Result<>(secKill);
    }

    /**
     * 去秒杀
     * @return
     */
//    @RequestMapping(value = "/toSecKill", method = RequestMethod.POST)
//    public Result<?> toSecKill(@RequestBody GoShopSeckillDto goShopSeckillDto) throws ParseException {
//        // 1.判断是否活动过期或者是活动还没开始
//        ShopSeckill shopSeckill = this.shopSeckillService.get(goShopSeckillDto.getProductId());
//        String nowDate = DateUtils.newDateTime();
//        if(shopSeckill.getBeginTime().compareTo(nowDate)>0 || shopSeckill.getEndTime().compareTo(nowDate)<0){
//            return new Result<>("没在秒杀活动时间内");
//        }
//        // 2.判断是否到达抢购上限
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long begintime = df.parse(shopSeckill.getBeginTime()).getTime();
//        long endtime = df.parse(shopSeckill.getEndTime()).getTime();
//        Long time=endtime-begintime;
//        if( this.stringRedisTemplate.opsForValue().get(shopSeckill.getId()+"_"+goShopSeckillDto.getUserId())==null){
//            this.stringRedisTemplate.opsForValue().set(shopSeckill.getId()+"_"+goShopSeckillDto.getUserId().toString(),"exist",time, TimeUnit.MILLISECONDS);
//        }else{
//            return new Result<>("您已到达抢购上限");
//        }
//        // 3.查询库存是否是大于0
//        if(shopSeckill.getStock() <= 0){
//            return new Result<>("商品已被抢光了");
//        }
//        try{
//            // 4.扣减库存
//            this.shopSeckillService.updateStock(goShopSeckillDto.getProductId());
//            // 5.创建订单
//            ShopOrder order = this.shopOrderService.createSecKillOrder(shopSeckill, goShopSeckillDto.getUserId());
//            // 发送延时消息
//            System.out.println("当前的时间是："+DateUtils.newDateTime());
//            this.rabbitTemplate.convertAndSend("dealy",order.getId(), new MessagePostProcessor() {
//                @Override
//                public Message postProcessMessage(Message message) throws AmqpException {
//                    message.getMessageProperties().setExpiration(Long.parseLong(shopSeckill.getCancelTime())*60*1000+"");
//                    return message;
//                }
//            });
//            System.out.println("消息发送成功");
//        }catch (Exception e){
//            throw new PochiException("秒杀失败");
//        }
//        return new Result<>("秒杀成功");
//    }

    /**
     * 去秒杀（优化1）
     * 因为接口效率的瓶颈在于数据库的操作。所以尽量减少对数据库的操作，优化方式：
     * 1.秒杀商品的库存放在redis里面，依次递减库存，并且redis的命令是原子性的，可以解决超卖问题
     * 2.所有的操作交给redis，redis内存型数据库效率大于直接操作mysql
     * 3.MQ异步创建订单，订单不在秒杀成功时候创建，只是让MQ发送个消息给创建订单接口
     * @return
     */
//    @RequestMapping(value = "/toSecKill", method = RequestMethod.POST)
//    public Result<?> toSecKill(@RequestBody @Validated GoShopSeckillDto goShopSeckillDto) throws ParseException {
//        // 1.判断是否活动过期或者是活动还没开始
//        if( this.stringRedisTemplate.opsForValue().get(goShopSeckillDto.getProductId().toString())==null) {
//            return new Result<>("没在秒杀活动时间内");
//        }
//        // 2.判断是否到达抢购上限
//        if( this.stringRedisTemplate.opsForValue().get(goShopSeckillDto.getProductId()+"_"+goShopSeckillDto.getUserId())==null){
//            this.stringRedisTemplate.opsForValue().set(goShopSeckillDto.getProductId()+"_"+goShopSeckillDto.getUserId().toString(),"exist",30*60*1000, TimeUnit.MILLISECONDS);
//        }else{
//            return new Result<>("您已到达抢购上限");
//        }
//        // 3.扣减库存并返回库存是否是大于0
//        if(this.stringRedisTemplate.opsForValue().decrement(goShopSeckillDto.getProductId().toString()) < 0){
//            this.stringRedisTemplate.opsForValue().increment(goShopSeckillDto.getProductId().toString());
//            return new Result<>("商品已被抢光了");
//        }
//        try{
//            // 4.MQ异步创建订单
//            this.rabbitTemplate.convertAndSend("direct","order", JSON.toJSONString(goShopSeckillDto));
//        }catch (Exception e){
//            // 消息发送出现异常还原库存
//            this.stringRedisTemplate.opsForValue().increment(goShopSeckillDto.getProductId().toString());
//            throw new PochiException("秒杀失败");
//        }
//        return new Result<>("秒杀成功");
//    }

    /**
     * 去秒杀（优化2）
     * 虽然秒杀的接口得到了提升，但是想想还是有地方可以加优化的：
     * 1.当秒杀商品卖完的时候，还是会有其他的线程去请求redis判断是否是卖完，虽然redis很快，到那时不断请求redis还是会有网络上的io消耗，
     * 所以可以加二级缓存，在本机加一个jvm缓存记录商品卖完的标志。
     * 2.我们的商品其实只有100个，我们没有必要让所有的请求全部都进来访问接口，可以采取令牌桶算法来限流。
     */
//    @RequestMapping(value = "/toSecKill", method = RequestMethod.POST)
//    public Result<?> toSecKill(@RequestBody @Validated GoShopSeckillDto goShopSeckillDto) throws ParseException {
//
//        // 1.使用令牌桶限流
//        if (!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)) {
//            return new Result<>("活动太过火爆,请重试!");
//        }
//        // 2.如果已经卖完不在请求redis，请求设置的第二级jvm缓存
//        if (getProductSoldOutMap().get(goShopSeckillDto.getProductId()) != null) {
//            return new Result<>("商品已被抢光了!");
//        }
//        // 3.判断是否活动过期或者是活动还没开始
//        if (this.stringRedisTemplate.opsForValue().get(goShopSeckillDto.getProductId().toString()) == null) {
//            return new Result<>("没在秒杀活动时间内!");
//        }
//        // 4.判断是否到达抢购上限
//        if (this.stringRedisTemplate.opsForValue().get(goShopSeckillDto.getProductId() + "_" + goShopSeckillDto.getUserId()) == null) {
//            this.stringRedisTemplate.opsForValue().set(goShopSeckillDto.getProductId() + "_" + goShopSeckillDto.getUserId().toString(), "exist", 30 * 60 * 1000, TimeUnit.MILLISECONDS);
//        } else {
//            return new Result<>("您已到达抢购上限!");
//        }
//        // 5.扣减库存并返回库存是否是大于0
//        if (this.stringRedisTemplate.opsForValue().decrement(goShopSeckillDto.getProductId().toString()) < 0) {
//            // 设置第二级缓存标志
//            productSoldOutMap.put(goShopSeckillDto.getProductId(), true);
//            this.stringRedisTemplate.opsForValue().increment(goShopSeckillDto.getProductId().toString());
//            return new Result<>("商品已被抢光了!");
//        }
//        try {
//            // 6.MQ异步创建订单
//            this.rabbitTemplate.convertAndSend("direct", "order", JSON.toJSONString(goShopSeckillDto));
//        } catch (Exception e) {
//            // 消息发送出现异常还原库存
//            this.stringRedisTemplate.opsForValue().increment(goShopSeckillDto.getProductId().toString());
//            if (productSoldOutMap.get(goShopSeckillDto.getProductId()) != null) {
//                productSoldOutMap.remove(goShopSeckillDto.getProductId());
//            }
//            throw new PochiException("秒杀失败!");
//        }
//        return new Result<>("秒杀成功!");
//    }

    /**
     * 去秒杀（优化3）
     * 虽然秒杀的接口得到了提升，但是想想还是有地方可以加优化的：
     * 1.启动多个服务用nginx来做负载均衡，这样由nginx将所有的请求发放在不同的服务上，减轻了单个服务器的压力，但是有一点，jvm缓存得不到同步，这时候引进zookeeper监听节点的变化
     * 2.搭建mysql集群，使mysql读写分离，对一些数据量会比较大的数据表，如商品表，订单表等进行分库分表，达到高可用和提升处理高并发的能力
     * 3.搭建redis集群，多个redis进行写入操作，也同样的达到高可用和提升处理高并发的能力
     */
    @RequestMapping(value = "/toSecKill", method = RequestMethod.POST)
    public Result<?> toSecKill(@RequestBody @Validated GoShopSeckillDto goShopSeckillDto) throws KeeperException, InterruptedException {

        // 1.使用令牌桶限流
        if (!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)) {
            return new Result<>("活动太过火爆,请重试!");
        }
        // 2.如果已经卖完不在请求redis，请求设置的第二级jvm缓存
        if (getProductSoldOutMap().get(goShopSeckillDto.getProductId()) != null) {
            return new Result<>("商品已被抢光了!");
        }
        // 3.判断是否活动过期或者是活动还没开始
        if (this.stringRedisTemplate.opsForValue().get(goShopSeckillDto.getProductId().toString()) == null) {
            return new Result<>("没在秒杀活动时间内!");
        }
        // 4.判断是否到达抢购上限
        if (this.stringRedisTemplate.opsForValue().get(goShopSeckillDto.getProductId() + "_" + goShopSeckillDto.getUserId()) == null) {
            this.stringRedisTemplate.opsForValue().set(goShopSeckillDto.getProductId() + "_" + goShopSeckillDto.getUserId().toString(), "exist", 30 * 60 * 1000, TimeUnit.MILLISECONDS);
        } else {
            return new Result<>("您已到达抢购上限!");
        }
        // 5.扣减库存并返回库存是否是大于0
        Long stock = this.stringRedisTemplate.opsForValue().decrement(goShopSeckillDto.getProductId().toString());
        if (stock <= 0) {
            if(stock != 0){
                this.stringRedisTemplate.opsForValue().increment(goShopSeckillDto.getProductId().toString());
                return new Result<>("商品已被抢光了!");
            }
            // 设置第二级缓存标志
            productSoldOutMap.put(goShopSeckillDto.getProductId(), true);
            // 设置zookeeper的节点监听
            String zkSoldOutProductPath = CoreConstant.getZKSoldOutProductPath(goShopSeckillDto.getProductId());
            // 如果是空的设置节点，不是空的把节点变成true
            if (zooKeeper.exists(zkSoldOutProductPath,true) == null){
                zooKeeper.create(zkSoldOutProductPath,"true".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }else if(new String(zooKeeper.getData(zkSoldOutProductPath, true, new Stat())).equals("false")){
                zooKeeper.setData(CoreConstant.getZKSoldOutProductPath(goShopSeckillDto.getProductId()),"true".getBytes(),-1);
            }
            // 监听zk售完标记节点
            zooKeeper.exists(zkSoldOutProductPath,true);
        }
        try {
            // 6.MQ异步创建订单
            this.rabbitTemplate.convertAndSend("direct", "order", JSON.toJSONString(goShopSeckillDto));
        } catch (Exception e) {
            // 消息发送出现异常还原库存
            this.stringRedisTemplate.opsForValue().increment(goShopSeckillDto.getProductId().toString());
            // 移除售完的缓存节点
            if (productSoldOutMap.get(goShopSeckillDto.getProductId()) != null) {
                productSoldOutMap.remove(goShopSeckillDto.getProductId());
            }
            // 变化zookeeper的值同步所有服务的售完节点
            if (zooKeeper.exists(CoreConstant.getZKSoldOutProductPath(goShopSeckillDto.getProductId()),true) != null){
                zooKeeper.setData(CoreConstant.getZKSoldOutProductPath(goShopSeckillDto.getProductId()),"false".getBytes(),-1);
            }
            throw new PochiException("秒杀失败!");
        }
        return new Result<>("秒杀成功!");
    }
}
