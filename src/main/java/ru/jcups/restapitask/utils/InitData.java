package ru.jcups.restapitask.utils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.service.impl.DefaultUserService;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@RequiredArgsConstructor
@Component
public class InitData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitData.class);
    private static final String packageName = "src/main/resources/utils/";

    private final DefaultUserService userService;
    private final ItemService itemService;
    private final BCryptPasswordEncoder encoder;

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
//        createUsers(49);
        initItems();
    }

    private List<String> loadListFromProperties(String fileName) {
        try (InputStreamReader stream = new InputStreamReader(
                new FileInputStream(packageName + fileName))) {
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
        String username = getUsername(firstName, lastName);
        String email = String.format("%s@mail.com", username);
        User user = User.builder()
                .firstName(firstName).lastName(lastName)
                .email(email).username(username)
                .password(firstName.toLowerCase())
                .roles(Set.of(Role.ROLE_USER))
                .age(age).build();
        printQuery(user);
        logger.debug("InitData.createRandomUser() returned: " + user);
        return user;
    }

    private String getUsername(String firstName, String lastName) {
        String username = firstName.toLowerCase().charAt(0) + "_" + lastName.toLowerCase();
        if (!userService.isUsernameNotUsed(username)) {
            for (int i = 1; i<firstName.length(); i++) {
                username = firstName.toLowerCase().charAt(i) + "_" + lastName.toLowerCase();
                if (userService.isUsernameNotUsed(username))
                    return username;
            }
            for (int i = 0; i<lastName.length(); i++) {
                username = lastName.toLowerCase().charAt(i)+"_"+firstName.toLowerCase();
                if (userService.isUsernameNotUsed(username))
                    return username;
            }
        }
        System.out.println("username = " + username);
        return username;
    }

    private void printQuery(User u) {
        System.out.printf("INSERT INTO users VALUES ('%s', '%s', %d, '%s', '%s', '%s');\n",
                u.getFirstName(), u.getLastName(), u.getAge(), u.getUsername(), u.getEmail(),
                encoder.encode(u.getFirstName().toLowerCase()));
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
