package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Client;

public interface ClientService extends IService<Client> {
    boolean verifyAndRegister(String token);
    String registerToken();

    Client findByToken(String token);
    Client findById(Integer id);
}
