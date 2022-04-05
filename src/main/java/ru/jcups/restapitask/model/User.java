package ru.jcups.restapitask.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends DefaultEntity implements UserDetails {

	String token;

	@Size(min = 4, max = 32, message = "Login length should be between 4 and 16 characters")
	@Column(unique = true, nullable = false)
	String username;

	@Email(message = "Please enter a valid email")
	@Column(unique = true, nullable = false)
	String email;

	@Size(min = 4, max = 128, message = "Password length should be between 4 and 128 characters")
	@Column(nullable = false)
	String password;

	@Transient
	String confirmPassword;

	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	Set<Role> roles;

	@OneToOne(cascade = CascadeType.ALL)
	Info info;

	public User() {
		this.info = new Info();
		this.token = UUID.randomUUID().toString();
	}

	public boolean isPasswordConfirmed(){
		return password.equals(confirmPassword);
	}

	public Role getMaxPriorityRole() {
		return roles.stream().max(Comparator.comparingInt(Enum::ordinal)).get();
	}

	@Override
	public void refresh(DefaultEntity entity) {
		User user = (User) entity;
		if (user.username != null)
			this.username = user.username;
		if (user.password != null)
			this.password = user.password;
		if (user.info != null)
			this.info.refresh(user.info);
		if (user.roles != null)
			this.roles = user.roles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Getter
	@Setter
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@AllArgsConstructor
	@Builder
	@Entity(name = "users_info")
	public static class Info extends DefaultEntity {

//		@OneToOne(mappedBy = "info")
//		User user;

		@Size(min = 2, max = 128, message = "First name length should be between 2 and 128 chars")
		String firstName;

		@Size(min = 2, max = 128, message = "Surname length should be between 2 and 128 chars")
		String lastName;

		@Min(value = 18, message = "If you are under the age of 18, please return when you reach the required age")
		Integer age;

		@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
		@JsonIgnoreProperties(value = "user")
		Bucket bucket;

		@OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
		List<Order> orders;

		public Info() {
			this.bucket = new Bucket();
		}

		@Override
		public void refresh(DefaultEntity entity) {
			Info info = (Info) entity;
			if (info.firstName != null)
				this.firstName = info.firstName;
			if (info.lastName != null)
				this.lastName = info.lastName;
			if (info.age != null)
				if (info.age >= 18)
					this.age = info.age;
		}
	}
}
