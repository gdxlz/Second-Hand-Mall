package com.hzf.controller;

import com.hzf.entity.ResultInfo;
import com.hzf.entity.User;
import com.hzf.repository.UserDao;
import com.hzf.service.UserService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @ResponseBody
    @GetMapping("/register")
    public ResultInfo register(HttpSession session, User user) {
        ResultInfo resultInfo = new ResultInfo();
        String checkCode = (String) session.getAttribute("CHECKCODE_SERVER");
        System.out.println(user);
        System.out.println(checkCode);
        boolean judgeCheckCode = userService.checkCode(checkCode, user);
        if (judgeCheckCode) {
            boolean judgeUsername = userService.findUsername(user);
            if (judgeUsername) {
                resultInfo.setErrorMsg("用户名已经存在 ");
            } else {
                userService.registe(user);
                resultInfo.setFlag(true);
            }
        } else {
            System.out.println("验证码错误");
            //model.addAttribute("msg","验证码错误");
            resultInfo.setErrorMsg("验证码错误");
            return resultInfo;
        }
        return resultInfo;
    }

    @GetMapping("/active")
    public void active(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        //ResultInfo resultInfo = new ResultInfo();
        final boolean judgeCode = userService.active(code);
        String msg=null;
        if (judgeCode) {
            msg="激活成功，请<a href='http://localhost:9090/hello/login.html'>登录</a>";
        } else {
            msg="激活失败";
        }
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(msg);
    }
    @PostMapping("/login")
    @ResponseBody
    public ResultInfo login(User user,HttpSession session){
        ResultInfo resultInfo=new ResultInfo();
        String checkCode = (String) session.getAttribute("CHECKCODE_SERVER");
        boolean judgeCheckCode = userService.checkCode(checkCode, user);
        if (judgeCheckCode){
            final boolean checkUsername = userService.findUsername(user);
            if (checkUsername){
                //User login = userService.login(user);
                ResultInfo resultInfo1 = userService.login(user);
                if (resultInfo1.isFlag()){
                    resultInfo.setFlag(true);
                    final User data = (User) resultInfo1.getData();
                    session.setAttribute("user",data);
                    return resultInfo;
                }else{
                    if (resultInfo1.getErrorMsg().equals("未激活")){
                        resultInfo.setErrorMsg("未激活");
                        return resultInfo;
                    }else{
                        resultInfo.setErrorMsg("密码错误");
                        return resultInfo;
                    }
                }
            }else{
                resultInfo.setErrorMsg("不存在这个用户名");
                return resultInfo;
            }
        }else{
            resultInfo.setErrorMsg("验证码错误");
            return resultInfo;
        }
    }

    @PostMapping("/findname")
    @ResponseBody
    public ResultInfo findname(HttpSession session){
        System.out.println("findname");
        ResultInfo resultInfo=new ResultInfo();
        User user = (User) session.getAttribute("user");
        if (user!=null){
            resultInfo.setFlag(true);
            resultInfo.setData(user);
            System.out.println(user);
        }else{

        }
        return resultInfo;
    }

    /**
     * @param request
     * @return
     * 展示当前登录的用户的信息，只需展示需要修改的信息；username，name，sex，telephone
     */
    @RequestMapping(value = "/userInfo")
    @ResponseBody
    public User userInfo(HttpServletRequest request){
        User curUser = (User)request.getSession().getAttribute("user");
        return curUser;
    }

    /**
     * @param request
     * @param user
     * @return
     * 修改用户的信息；；username，name，sex，telephone
     */
    @RequestMapping(value = "/updateInfo")
    public String updateInfo(HttpServletRequest request, User user) {
        User curuser = (User)request.getSession().getAttribute("user");
//        System.err.println(curuser);
        System.out.println(user);
        User newUser=new User();
        newUser.setUid(curuser.getUid());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        newUser.setTelephone(user.getTelephone());
        newUser.setSex(user.getSex());
        newUser.setBirthday(user.getBirthday());
        System.out.println(newUser);
        userService.updateInfo(newUser);
//        最后要记得修改会话中的user,这样可以不用重新登录就显示更新的用户信息
        request.getSession().setAttribute("user", newUser);
        return "redirect:/hello/index";
    }

}
