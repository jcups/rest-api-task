package ru.jcups.restapitask.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.jcups.restapitask.model.Guest;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.utils.ItemParser;

import javax.servlet.http.HttpSession;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final ItemService itemService;

    @GetMapping("")
    public String index(HttpSession session, Guest guest) {
        logger.info("HomeController.index");
        logger.info("index() called with: session = [" + session + "], guest = [" + guest + "]");
        return "index";
    }

    @GetMapping("/market")
    public String market() {
        logger.info("HomeController.market");
        return "market";
    }

    @GetMapping("/market/{id}")
    public String item(@PathVariable long id, Model model){
        logger.info("HomeController.item");
        logger.info("item() called with: id = [" + id + "], model = [" + model + "]");
        try {
            model.addAttribute("item", itemService.getById(id));
            model.addAttribute("ratable", itemService.getFourItemsWithBiggestRate());
        } catch (ObjectNotFoundException e){
            e.printStackTrace();
            return "redirect:/market";
        }
        return "item";
    }

    @GetMapping("/add")
    public String add(@RequestParam String url, Authentication authentication) {
        logger.info("HomeController.add");
        logger.info("add() called with: url = [" + url + "], authentication = [" + authentication + "]");
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();
            if (roles.contains(Role.ROLE_ADMIN)||roles.contains(Role.ROLE_CREATOR)) {
                try {
                    itemService.create(ItemParser.parseItem(url));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return "index";
    }
}
