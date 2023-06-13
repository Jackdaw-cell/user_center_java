package com.jackdaw.javapro.service;

import com.jackdaw.javapro.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.model.dto.TeamQuery;
import com.jackdaw.javapro.model.request.TeamJoinRequest;
import com.jackdaw.javapro.model.request.TeamQuitRequest;
import com.jackdaw.javapro.model.request.TeamUpdateRequest;
import com.jackdaw.javapro.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Jackdaw
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-05-12 11:52:22
*/
public interface TeamService extends IService<Team> {


    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 分页显示队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User user);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍（删除队伍）
     * @param id
     * @return
     */
    boolean deleteTeam(Long id, User loginUser);
}
