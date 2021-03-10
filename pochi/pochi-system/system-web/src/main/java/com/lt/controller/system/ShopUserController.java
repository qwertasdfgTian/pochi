package com.lt.controller.system;

import com.lt.config.shiro.ShopUserRealm;
import com.lt.config.shiro.UserToken;
import com.lt.dto.UserDto;
import com.lt.utils.ShiroUtils;
import com.lt.vo.*;
import com.lt.pojo.ShopUser;
import com.lt.service.ShopUserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/24 17:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
public class ShopUserController {

    @Autowired
    private ShopUserService shopUserService;

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<ShopUser>> getByPage(@RequestBody Page<ShopUser> page) {
        page = this.shopUserService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<ShopUserVo> get(@PathVariable Long id) {
        ShopUserVo shopUserVo = this.shopUserService.get(id);
        return new Result<>(shopUserVo);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.shopUserService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 解封
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/enableById/{id}", method = RequestMethod.PUT)
    public Result<?> enableById(@PathVariable Long id) {
        this.shopUserService.enableById(id);
        return new Result<>("操作成功");
    }

    /**
     * 封禁
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/disableById/{id}", method = RequestMethod.PUT)
    public Result<?> disableById(@PathVariable Long id) {
        this.shopUserService.disableById(id);
        return new Result<>("操作成功");
    }

    /**
     * 查询累计消费最多的前十个用户
     *
     * @return
     */
    @RequestMapping(value = "/getTopStatistic", method = RequestMethod.GET)
    public Result<List<ShopUserStatisticVo>> getTopStatistic() {
        List<ShopUserStatisticVo> list = this.shopUserService.getTopStatistic();
        return new Result<>(list);
    }

    /**
     * 根据创建人查询我的钱包
     * @return
     */
    @RequestMapping(value = "/getMyWallet", method = RequestMethod.GET)
    public Result<?> getMyWallet() {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        MyWalletVo myWalletVo = this.shopUserService.getMyWallet(loginUser);
        return new Result<>(myWalletVo);
    }

    /**
     * 根据创建人修改用户
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<?> update(@RequestBody UserDto userDto) {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        LoginUser loginUser1=this.shopUserService.update(loginUser,userDto);
        // 更新当前登录用户
        ShiroUtils.setUser(loginUser1);
        return new Result<>("修改成功",loginUser1);
    }
}
