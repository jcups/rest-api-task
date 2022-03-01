package ru.jcups.restapitask.utils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class InitData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitData.class);

    private final UserService userService;
    private final ItemService itemService;

    private final List<String> uris = List.of(
            "https://komp.1k.by/mobile-notebooks/asus/ASUS_ROG_Strix_G15_G513IH_HN014-4492628.html",
            "https://komp.1k.by/mobile-notebooks/msi/MSI_Bravo_15_B5DD_040XRU-4527314.html",
            "https://komp.1k.by/mobile-notebooks/asus/ASUS_ROG_Strix_G15_G513IE_HN003-4573803.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_IdeaPad_Gaming_3_15ACH6_82K200LSRK-4564248.html",
            "https://komp.1k.by/mobile-notebooks/asus/ASUS_TUF_Gaming_F15_FX506LH_HN002-4440395.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_IdeaPad_5_Pro_14ACN6_82L7000PRK-4559232.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_P17_Gen_2_20YU0022RT-4543227.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_P17_Gen_1_20SN001MRT-4399403.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_Legion_7_16ITHg6_82K6000DRU-4548425.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_T15g_Gen_1_20UR002TRT-4482246.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_P1_Gen_3_20TH0016RT-4440368.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_X1_Extreme_Gen_3_20TK0030RT-4494130.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_P15_Gen_1_20ST005URT-4340527.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_Legion_7_16ACHg6_82N6000DRU-4508365.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_Legion_Y740_17IRHg_81UJ008SRU-4174049.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_Legion_7_15IMHg05_81YU0077RK-4317381.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_X1_Titanium_Yoga_Gen_1_20QA001SRT-4495741.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_ThinkPad_X1_Carbon_6_20KH0079RT-3496867.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_Legion_5_15ITH6H_82JH005GRE-4561770.html",
            "https://komp.1k.by/mobile-notebooks/lenovo/Lenovo_Legion_S7_15ACH6_82K8001FRK-4548428.html"
    );

    private final List<String> names = List.of("Aaron", "Abraham", "Adam", "Alan", "Albert", "Alejandro", "Alex",
            "Alexander", "Alfred", "Andrew", "Angel", "Anthony", "Antonio", "Adrian", "Aidan", "Ashton",
            "Austin", "Joyce", "Jacqueline", "Jessica", "Jada", "Jane", "Jasmine", "Jenna", "Jennifer",
            "Jocelyn", "Jordan", "Josephine", "Julia");

    private final List<String> lastNames = List.of("Anderson", "Annese", "Annon", "Anstead", "Anstey", "Anthony",
            "Appleford", "Applegate", "Appleton", "Appleyard", "Arbour", "Arch", "Archer", "Ardley",
            "Ardron", "Arliss", "Aylesworth", "Armfield", "Armistead", "Armitstead", "Armstrong",
            "Arnold", "Ayres", "Artell", "Arterton", "Arthur", "Artley", "Asbridge", "Ayris", "Ashbridge",
            "Ashby", "Ashdown", "Asher", "Ashfield", "Ashley-Cooper", "Ashpitel", "Ashworth", "Aspey",
            "Asplin", "Assheton", "Astle", "Astley", "Atlee", "Atkin", "Atkins", "Atkinson", "Attrill",
            "Auger", "Austen", "Austen-Leigh", "Auster", "Austin", "Avey", "Aveyard", "Avory", "Awford",
            "Axford", "Axon", "Axtell", "Axton", "Aykroyd", "Aymes", "Ayrton");

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        logger.info("InitData.run");
        logger.info("run() called with: args = [" + Arrays.toString(args) + "]");
        createAdmin();
        createUsers(9);
        initItems();
    }

    private void initItems() {
        logger.info("InitData.initItems");
        uris.stream().map(ItemParser::parseItem).peek(item -> System.out.println("item = " + item))
                .forEach(itemService::create);
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
