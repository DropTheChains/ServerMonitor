package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Client;
import com.example.entity.dto.ClientDetail;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private String registerToken = this.generateNewToken();

    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();
    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();

    @Resource
    ClientDetailMapper detailMapper;


    @Override
    public boolean verifyAndRegister(String token) {
        if (this.registerToken.equals(token)) {
            System.out.println(token);
            int id = this.randomClientId();
            Client client = new Client(id,"未命名主机",token,new Date());
            if (this.save(client)){
                registerToken = this.generateNewToken();
                this.addClientCache(client);
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    public void initClientCache(){
        this.list().forEach(this::addClientCache);
    }

    private void addClientCache(Client client){
        clientIdCache.put(client.getId(),client);
        clientTokenCache.put(client.getToken(),client);
    }

    private int randomClientId() {
        return new Random().nextInt(90000000) + 10000000;
    }

    @Override
    public String registerToken() {
        return registerToken;
    }

    @Override
    public Client findByToken(String token) {
        return clientTokenCache.get(token);
    }

    @Override
    public Client findById(Integer id) {
        return clientIdCache.get(id);
    }

    @Override
    public void updateClientDetail(ClientDetailVO vo, Client client) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo,detail);
        detail.setId(client.getId());
        if (Objects.nonNull(detailMapper.selectById(client.getId()))){
            detailMapper.updateById(detail);
        }else {
            detailMapper.insert(detail);
        }
    }

    private String generateNewToken() {
        String CHARACTERS = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMOPARSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder tokenBuilder = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            tokenBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        System.out.println(tokenBuilder);
        return tokenBuilder.toString();
    }
}
