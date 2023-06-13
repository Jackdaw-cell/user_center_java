package com.jackdaw.javapro.controller;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(value = "鱼聪明api")
@RestController
@RequestMapping("/yupi")
@Slf4j
public class YvaiController {
    @Resource
    private YuCongMingClient client;

    @GetMapping("/flower")
    public BaseResponse<DevChatResponse> flower(@RequestParam String text){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1651466565113200642L);
        devChatRequest.setMessage(text);
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        return response;
    }

    @GetMapping("/docter")
    public BaseResponse<DevChatResponse> docter(@RequestParam String text){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1660740194803908609L);
        devChatRequest.setMessage(text);
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        return response;
    }

    @GetMapping("/sale")
    public BaseResponse<DevChatResponse> sale(@RequestParam String text){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1651469426689687553L);
        devChatRequest.setMessage(text);
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        return response;
    }

    @GetMapping("/law")
    public BaseResponse<DevChatResponse> law(@RequestParam String text){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1651472895748194305L);
        devChatRequest.setMessage(text);
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        return response;
    }


}
