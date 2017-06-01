package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by GYX on 2017/5/26.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list",seckillList);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") long seckillId,Model model){
        if(StringUtils.isEmpty(seckillId)){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getSeckillById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
        SeckillResult<Exposer> result = null;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/excution",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExcution> excute(@PathVariable("seckillId") long seckillId,
           @PathVariable("md5")String md5,@CookieValue(value = "killPhone",required = false) long phone){
        if(StringUtils.isEmpty(phone)){
            return new SeckillResult<SeckillExcution>(false,"未注册");
        }
        try{
            SeckillExcution seckillExcution = seckillService.excuteSeckill(seckillId, phone, md5);
            return  new SeckillResult<SeckillExcution>(true,seckillExcution);
        }catch (RepeatKillException e){
            SeckillExcution excution = new SeckillExcution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExcution>(false,excution);
        }catch (SeckillCloseException e){
            SeckillExcution excution = new SeckillExcution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExcution>(false,excution);
        }catch (Exception e){
            SeckillExcution excution = new SeckillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExcution>(false,excution);
        }
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        return new SeckillResult<Long>(true,new Date().getTime());
    }
}
