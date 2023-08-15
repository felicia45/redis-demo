package cn.indix.hfx.demo.controller;

import cn.indix.hfx.demo.util.RedisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hfx
 * @version 1.0.0
 * @Description:
 * @Date: 2023/08/11 10:49
 */
@RestController
public class TestController {


    @GetMapping("test")
    public String test() {
        return RedisUtil.set("key", "value");
    }

}
