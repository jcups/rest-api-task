package ru.jcups.restapitask.controller.rest;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.utils.ItemParser;

import java.util.Set;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

    private static final Logger logger = LoggerFactory.getLogger(MarketController.class);

    private final ItemService itemService;

    @GetMapping("")
    public ModelAndView market() {
        logger.info("MarketController.market");
        return new ModelAndView("market");
    }

    @GetMapping("/{id}")
    public ModelAndView item(@PathVariable long id, Model model) {
        logger.info("MarketController.item");
        logger.info("item() called with: id = [" + id + "], model = [" + model + "]");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("item");
        try {
            model.addAttribute("item", itemService.getById(id));
//            model.addAttribute("ratable", itemService.getItemsLimitFour());
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            mav.setViewName("redirect:/market");
            return mav;
        }
        mav.getModel().putAll(model.asMap());
        return mav;
    }

    @PatchMapping("/add")
    public String addItem(@RequestParam String url, Authentication authentication) {
        logger.info("MarketController.addItem");
        logger.info("add() called with: url = [" + url + "], authentication = [" + authentication + "]");
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Set<Role> roles = user.getRoles();
            if (roles.contains(Role.ROLE_ADMIN) || roles.contains(Role.ROLE_CREATOR)) {
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
