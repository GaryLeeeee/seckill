package com.garylee.seckill.controller;

import com.garylee.seckill.domain.Seckill;
import com.garylee.seckill.dto.Exposer;
import com.garylee.seckill.dto.Result;
import com.garylee.seckill.dto.SeckillExecution;
import com.garylee.seckill.exception.RepeatKillException;
import com.garylee.seckill.exception.SeckillCloseException;
import com.garylee.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by GaryLee on 2019-02-07 21:47.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    SeckillService seckillService;

    //输出秒杀商品列表
    @RequestMapping("/list")
    public String list(Model model) {
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    //跳转到单个商品详情页
    @RequestMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null)
            return "redirect:/seckill/list";
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null)
            return "redirect:/seckill/list";
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    //根据seckillId判断是否开启秒杀
    @PostMapping("/{seckillId}/exposer")
    @ResponseBody
    public Result exposer(@PathVariable("seckillId") Long seckillId) {
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed())
            return Result.success(exposer);
        return Result.fail("秒杀请求错误!");
    }

    //执行秒杀操作
    @PostMapping("/{seckillId}/{md5}/execution")
    @ResponseBody
    public Result execute(@PathVariable("seckillId") Long seckillId,
                          @PathVariable("md5") String md5,
                          @CookieValue("killPhone") Long phone) {
        //如果没登陆
        if (phone == null)
            return Result.fail("未登录!");
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return Result.success(execution);
        }catch (RepeatKillException e1){
            return Result.fail("重复秒杀!");
        }catch (SeckillCloseException e2){
            return Result.fail("秒杀已关闭!");
        }catch (Exception e){
            return Result.fail("系统异常!");
        }
    }

    //获取当前时间
    @GetMapping("/time/now")
    @ResponseBody
    public Result time(){
        Date date = new Date();
        return Result.success(date.getTime());
    }
}
