package ru.jcups.restapitask.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.jcups.restapitask.model.Bucket;
import ru.jcups.restapitask.model.Role;
import ru.jcups.restapitask.model.User;
import ru.jcups.restapitask.service.ItemService;
import ru.jcups.restapitask.service.impl.DefaultUserService;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TestDataInitializer implements CommandLineRunner {

	private static final String packageName = "src/main/resources/utils/";
	private static List<String> uris;
	private static List<String> names;
	private static List<String> lastNames;
	private final DefaultUserService userService;
	private final ItemService itemService;
	private final BCryptPasswordEncoder encoder;
	private final Random random = new Random();

	@PostConstruct
	public void initData() {
		uris = initUris();
		names = initNames();
		lastNames = initLastNames();
	}

	@Override
	public void run(String... args) {
		createAdmin();
//        createUsers(49);
//		initItems();
	}

	private List<String> loadListFromProperties(String fileName) {
		try (InputStreamReader stream = new InputStreamReader(
				new FileInputStream(packageName + fileName))) {
			Properties properties = new Properties();
			properties.load(stream);
			return properties.values().stream().map(o -> (String) o)
					.collect(Collectors.toList());
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
		uris.stream().map(ItemParser::parseItem).forEach(itemService::create);
	}

	private void createUsers(int count) {
		for (int i = 1; i <= count; i++) {
			User user = createRandomUser();
			try {
				userService.create(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private User createRandomUser() {
		int age = 18 + random.nextInt(47);
		String firstName = names.get(random.nextInt(names.size()));
		String lastName = lastNames.get(random.nextInt(names.size()));
		String username = getUsername(firstName, lastName);
		String email = String.format("%s@mail.com", username);
		User user = User.builder()
				.email(email).username(username)
				.password(firstName.toLowerCase())
				.roles(Set.of(Role.ROLE_USER)).build();
		printQuery(user);
		return user;
	}

	private String getUsername(String firstName, String lastName) {
		String username = firstName.toLowerCase().charAt(0) + "_" + lastName.toLowerCase();
		if (!userService.isUsernameNotUsed(username)) {
			for (int i = 1; i < firstName.length(); i++) {
				username = firstName.toLowerCase().charAt(i) + "_" + lastName.toLowerCase();
				if (userService.isUsernameNotUsed(username))
					return username;
			}
			for (int i = 0; i < lastName.length(); i++) {
				username = lastName.toLowerCase().charAt(i) + "_" + firstName.toLowerCase();
				if (userService.isUsernameNotUsed(username))
					return username;
			}
		}
		return username;
	}

	private void printQuery(User u) {
		System.out.printf("INSERT INTO users VALUES ('%s', '%s', '%s');\n",
				u.getUsername(), u.getEmail(),
				encoder.encode(u.getEmail().substring(0, 3).toLowerCase()));
	}

	private void createAdmin() {
		userService.create(User.builder()
				.roles(Set.of(Role.ROLE_ADMIN))
				.username("admin")
				.email("admin@gmail.com")
				.password("pass")
				.confirmPassword("pass")
				.token("00000")
				.info(User.Info.builder()
						.firstName("user")
						.lastName("name")
						.age(22)
						.bucket(new Bucket())
						.build())
				.build());
	}
}
