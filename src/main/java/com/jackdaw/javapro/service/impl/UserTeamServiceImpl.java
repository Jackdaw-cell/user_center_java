package com.jackdaw.javapro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackdaw.javapro.model.domain.UserTeam;
import com.jackdaw.javapro.service.UserTeamService;
import com.jackdaw.javapro.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Jackdaw
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-05-12 11:53:11
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




