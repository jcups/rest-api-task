package ru.jcups.restapitask.utils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.service.UserService;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@RequiredArgsConstructor
//@Component
public class InitData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitData.class);
    private static final String packageName = "src/main/resources/utils/";

    private final UserService userService;
    private final ItemService itemService;

    private static List<String> uris;

    private static List<String> names;

    private static List<String> lastNames;

    private final Random random = new Random();

    @PostConstruct
    public void initData() {
        logger.info("InitData.initData");
        uris = initUris();
        names = initNames();
        lastNames = initLastNames();
    }

    @Override
    public void run(String... args) {
        logger.info("InitData.run");
        logger.info("run() called with: args = [" + Arrays.toString(args) + "]");
//        createAdmin();
//        createUsers(9);
//        initItems();
    }

    private List<String> loadListFromProperties(String fileName) {
        try (InputStreamReader stream = new InputStreamReader(
                new FileInputStream(packageName+fileName))) {
            Properties properties = new Properties();
            properties.load(stream);
            LinkedList<String> list = new LinkedList<>();
            for (int i = 0; i < properties.size(); i++) {
                list.add(properties.getProperty(String.valueOf(i)));
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private List<String> initLastNames() {
        return loadListFromProperties("lastNames.properties");
    }

    private List<String> initNames() {
        return loadListFromProperties("names.properties");
    }

    private List<String> initUris() {
        return loadListFromProperties("urls.properties");
    }

    private void initItems() {
        logger.info("InitData.initItems");
        uris.stream().map(ItemParser::parseItem).forEach(itemService::create);
    }

    private void createUsers(int count) {
        logger.info("InitData.createUsers");
        logger.info("createUsers() called with: count = [" + count + "]");
        for (int i = 1; i <= count; i++) {
            User user = createRandomUser();
            try {
                userService.create(user);
            } catch (Exception e) {
                logger.error("InitData.run: ", e);
            }
        }
    }

    private User createRandomUser() {
        logger.info("InitData.createRandomUser");
        int age = 18 + random.nextInt(47);
        String firstName = names.get(random.nextInt(names.size()));
        String lastName = lastNames.get(random.nextInt(names.size()));
        String email = String.format("%s.%s.%d@mail.com", firstName, lastName, age);
        String username = firstName + "-" + lastName + "-" + age;
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .username(username)
                .password(firstName)
                .roles(Set.of(Role.ROLE_USER))
                .age(age)
                .build();
        logger.debug("InitData.createRandomUser() returned: " + user);
        return user;
    }

    private void createAdmin() {
        logger.info("InitData.createAdmin");
        userService.create(User.builder()
                .firstName("name")
                .lastName("lastName")
                .age(21)
                .roles(Set.of(Role.ROLE_ADMIN))
                .username("admin")
                .email("admin@gmail.com")
                .password("pass")
                .build());
    }
}
