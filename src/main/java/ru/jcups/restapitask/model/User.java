package ru.jcups.restapitask.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends DefaultEntity implements UserDetails {

    @NotNull
    @Size(min = 2, max = 128, message = "First name length should be between 2 and 128 chars")
    String firstName;

    @NotNull
    @Size(min = 2, max = 128, message = "Surname length should be between 2 and 128 chars")
    String lastName;

    @NotNull
    @Min(value = 18, message = "If you are under the age of 18, please return when you reach the required age")
    int age;

    @Email(message = "Please enter a valid email")
    @Column(unique = true, nullable = false)
    String email;

    @Size(min = 4, max = 32, message = "Login length should be between 4 and 16 characters")
    @Column(unique = true, nullable = false)
    String username;

    @NotNull
    @Size(min = 4, max = 128, message = "Password length should be between 4 and 128 characters")
    String password;

    @Transient
    String confirmPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = "user")
    Bucket bucket;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    List<Order> orders;

    @Override
    public void refresh(DefaultEntity entity) {
        User user = (User) entity;
        if (user.firstName != null)
            this.firstName = user.firstName;
        if (user.lastName != null)
            this.lastName = user.lastName;
        if (user.age >= 18)
            this.age = user.age;
        if (user.username != null)
            this.username = user.username;
        if (user.password != null)
            this.password = user.password;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.getId() && age == user.age &&
                firstName.equals(user.firstName) && lastName.equals(user.lastName)
                && email.equals(user.email) && username.equals(user.username)
                && password.equals(user.password) && roles.equals(user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, age, email, username, password, roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", surname='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + roles +
                "}\n";
    }
}
