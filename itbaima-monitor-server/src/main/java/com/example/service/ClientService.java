package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;

public interface ClientService extends IService<Client> {
    boolean verifyAndRegister(String token);
    String registerToken();

    Client findByToken(String token);
    Client findById(Integer id);

    void updateClientDetail(ClientDetailVO vo,Client client);
}
