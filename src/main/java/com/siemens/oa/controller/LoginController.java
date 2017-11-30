package com.siemens.oa.controller;

import com.siemens.oa.entity.User;
import com.siemens.oa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gxurn9 on 11/22/2017.
 */
@Controller
public class LoginController {
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String beforeLogin() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Map<String, Object> loginPost(String userName, String password, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        User user = userService.selectUserByName(userName);
        if (user != null) {
            if (password.equals(user.getPassword())) {
                session.setAttribute(WebSecurityConfig.SESSION_KEY, userName);
                System.out.println(session.getAttribute(WebSecurityConfig.SESSION_KEY));
                map.put("success", true);
                map.put("message", "login in successful");
                map.put("code", "200");
                return map;
            } else {
                map.put("success", false);
                map.put("message", "Username or password false");
                map.put("code", "403");
                return map;
            }
        } else {
            map.put("success", false);
            map.put("message", "User not exist!");
            map.put("code", "404");
            return map;
        }

    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "redirect:/login";
    }
}
