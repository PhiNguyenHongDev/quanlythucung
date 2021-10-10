package com.example.quanlythucung.app.security;

import com.example.quanlythucung.domain.model.User;
import com.example.quanlythucung.domain.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class SignIn {
    @Inject
    RestFB restFB;
    @Inject
    UserService userService;

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String login() {
        return "login";
    }

    @RequestMapping("/login-facebook")
    public String loginFacebook(HttpServletRequest request) {
        String code = request.getParameter("code");
        String accessToken = "";
        try {
            accessToken = restFB.getToken(code);
        } catch (IOException e) {
            return "redirect:/login?message=facebook_denied";
        }
        com.restfb.types.User user = restFB.getUserInfo(accessToken);
        UserDetails userDetail = restFB.buildUser(user);
        User user1 = userService.findOne(user.getId());
        if (user1 == null) {
//            userService.create(user.getId(), "defaultPassword");
            userService.create(null);
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/";
    }

    @RequestMapping(value = "/signup", params = "form")
    public String signupForm() {
        return "login/SignUp";
    }

    @RequestMapping(value = "/signup", params = {"submit", "username", "password"})
    public String signup(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
//        userService.create(username, password);
        userService.create(null);
        return "/login";
    }
}
