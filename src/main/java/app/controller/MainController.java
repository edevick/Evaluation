package app.controller;

import app.model.Member;
import app.model.UserContact;
import app.service.impl.MemberServiceImpl;
import app.service.impl.UserServiceImpl;
import app.utils.EncrytedPasswordUtils;
import app.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    private String errorMessage = null;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String welcomePage(WebRequest webRequest, Model model) {

        if (webRequest.getParameter("lang") != null)
            Locale.setDefault(Locale.forLanguageTag(webRequest.getParameter("lang")));
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", messageSource.getMessage("welcome", null, Locale.getDefault()));
        return "welcomePage";
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(Model model, Principal principal) {
        return "redirect:/?lang=" + Locale.getDefault();
    }


    @RequestMapping(value = "/jury", method = RequestMethod.GET)
    public String juryList(Model model, Principal principal) {
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        List<app.model.User> juries = userService.findAllUsers();
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("juries", juries);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "jury";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {

        System.out.println(Locale.getDefault());
        return "loginPage";
    }


    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {

        String userName = principal.getName();

        System.out.println("User Name: " + userName);

        User loginedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        return "main";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {

        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();

            String userInfo = WebUtils.toString(loginedUser);

            model.addAttribute("userInfo", userInfo);

            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);

        }
        return "403Page";
    }

    @RequestMapping(value = "/jury/form")
    public String showform(Model model) {
        model.addAttribute("command", new app.model.User());
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "juryform";
    }

    @RequestMapping(value = "/jury/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("user") app.model.User user, @ModelAttribute("user_contacts") UserContact contact) {
        if (userService.findUserByLogin(user.getLogin()) != null) {
            errorMessage = messageSource.getMessage("error.addUserWithExistsLogin", null, Locale.getDefault());
            return "redirect:/jury/form?lang=" + Locale.getDefault();
        } else {
            user.setEncrytedPassword(EncrytedPasswordUtils.encrytePassword(user.getEncrytedPassword()));
            user.setUserContact(contact);
            userService.save(user);
            return "redirect:/jury?lang=" + Locale.getDefault();
        }
    }

    @RequestMapping(value = "/jury/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        app.model.User user = userService.findUserById(id);
        model.addAttribute("jury", user);
        return "editform";
    }

    @RequestMapping(value = "/jury/editsave", method = RequestMethod.POST)
    public String editsave(@ModelAttribute("jury") app.model.User user, @ModelAttribute("user_contacts") UserContact contact) {
        //Проверка: изменился ли пароль? если да, тогда перекодировать его и сохранить в базе
        if (!userService.findUserById(user.getUserId()).getEncrytedPassword().equals(user.getEncrytedPassword())) {
            user.setEncrytedPassword(EncrytedPasswordUtils.encrytePassword(user.getEncrytedPassword()));
        }
        user.setUserContact(contact);
        userService.delete(userService.findUserById(user.getUserId()));
        userService.save(user);
        return "redirect:/jury?lang=" + Locale.getDefault();
    }

    @RequestMapping(value = "/jury/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable long id) {
        app.model.User user = userService.findUserById(id);
        if (user.getRole().equals("admin") && userService.findAllAdmins().size() == 1) {
            errorMessage = messageSource.getMessage("error.deleteLastAdmin", null, Locale.getDefault());
        } else {
            userService.delete(user);
        }
        return "redirect:/jury?lang=" + Locale.getDefault();
    }

    // Members
    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public String memberList(Model model) {
        List<Member> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "members";
    }

    @RequestMapping(value = "/member/form")
    public String memberForm(Model model) {
        model.addAttribute("command", new app.model.User());
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            errorMessage = null;
        }
        return "memberform";

    }
}
