package ru.jcups.restapitask.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.jcups.restapitask.model.Guest;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.impl.DefaultUserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final DefaultUserService userService;

    @GetMapping
    public String registration(Model model) {
        logger.info("RegistrationController.registration");
        logger.info("registration() called with: model = [" + model + "]");
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping
    public String registrationNewUser(@Valid User user, HttpSession session) {
        logger.info("RegistrationController.registrationNewUser");
        logger.info("registrationNewUser() called with: user = [" + user + "], session = [" + session + "]");
        if (user.getPassword().equals(user.getConfirmPassword())) {
            Guest guest = (Guest) session.getAttribute("guest");
            if (guest !=null){
                user.getInfo().setBucket(guest.getBucket());
            }
            userService.create(user);
            return "redirect:/";
        } else {
            return "registration";
        }
    }
}
