package com.jackdaw.javapro.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户加入队伍
 * @author Jackdaw
 */
@Data
public class TeamJoinRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}