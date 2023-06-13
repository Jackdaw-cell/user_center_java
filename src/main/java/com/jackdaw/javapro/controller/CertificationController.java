//package com.jackdaw.javapro.controller;
//
//import com.jackdaw.javapro.common.BaseResponse;
//import com.jackdaw.javapro.common.ErrorCode;
//import com.jackdaw.javapro.common.ResultUtils;
//import com.jackdaw.javapro.exception.BusinessException;
//import com.jackdaw.javapro.model.domain.User;
//import com.jackdaw.javapro.model.onlineMessage.Result;
//import com.jackdaw.javapro.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//import static com.jackdaw.javapro.constant.UserConstant.USER_LOGIN_STATE;
//
///**
// * @author Jackdaw
// */
//@RestController
////模拟登录操作
//@Slf4j
//@RequestMapping("/online")
//public class CertificationController {
//
//    @Resource
//    private UserService userService;
//
//    @Resource
//    private RedisTemplate<String,Object> redisTemplate;
//
//    @GetMapping("/toLogin")
//    public BaseResponse<User> toLogin(HttpServletRequest request){
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser=(User) userObj;
//        long userId=currentUser.getId();
//        if (currentUser==null){
//            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
//        }
////        设置会话的最大非活动时间间隔，30分钟内没有与服务器进行任何交互，会话将过期并被销毁
//        request.getSession().setMaxInactiveInterval(30*60);
//        log.info("用户:"+userId+"登录验证中..");
//        User user=userService.getById(userId);
//        return ResultUtils.success(user);
//
////        if ("张三".equals(user)&&"123".equals(pwd)){
////            result.setFlag(true);
////            log.info(user+"登录验证成功");
//////            httpSession.setAttribute("user",user);
////            request.getSession().setAttribute("user",user);
////        }else if ("李四".equals(user)&&"123".equals(pwd)){
////            result.setFlag(true);
////            log.info(user+"登录验证成功");
//////            httpSession.setAttribute("user",user);
////            request.getSession().setAttribute("user",user);
////        }else if ("田七".equals(user)&&"123".equals(pwd)){
////            result.setFlag(true);
////            log.info(user+"登录验证成功");
//////            httpSession.setAttribute("user",user);
////            request.getSession().setAttribute("user",user);
////        }
////        else if ("王五".equals(user)&&"123".equals(pwd)){
////            result.setFlag(true);
////            log.info(user+"登录验证成功");
//////            httpSession.setAttribute("user",user);
////            request.getSession().setAttribute("user",user);
////        }else {
////            result.setFlag(false);
////            log.info(user+"验证失败");
////            result.setMessage("登录失败");
////        }
////        return result;
//    }
//    @GetMapping("/getUsername")
//    public String getUsername(HttpServletRequest request){
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser=(User) userObj;
//        long userId=currentUser.getId();
//        if (currentUser==null){
//            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
//        }
////        设置会话的最大非活动时间间隔，30分钟内没有与服务器进行任何交互，会话将过期并被销毁
//        request.getSession().setMaxInactiveInterval(30*60);
//        log.info("用户:"+userId+"登录验证中..");
//        User user=userService.getById(userId);
//        return user.getUsername();
//    }
//}
