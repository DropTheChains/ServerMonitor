package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ClientService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class ClientController {

    @Resource
    ClientService clientService;

    @RequestMapping("/register")
    public RestBean<String> register(@RequestHeader("Authorization") String token){
        return clientService.verifyAndRegister(token) ?
                RestBean.success() : RestBean.failure(401,"客户端注册失败，请检查token是否正确");
    }
}
