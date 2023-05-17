package com.jackdaw.javapro.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍
 * @author Jackdaw
 */
@Data
public class TeamQuitRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long teamId;

}