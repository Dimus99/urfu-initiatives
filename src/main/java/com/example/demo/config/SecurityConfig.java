package com.example.demo.config;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repos.UserRepo;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.HashSet;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .logout()
                .logoutSuccessUrl("/").and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/about").permitAll()
                //.antMatchers("/users").hasRole(Role.ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
        .loginPage("/login").permitAll()
        .defaultSuccessUrl("/");
    }

    @Bean
    public PrincipalExtractor principalExtractor(UserRepo userRepo){
        return map -> {
            String  id =  (String) map.get("sub");
            User user = userRepo.findById(id).orElseGet(()->{
                User newUser = new User();
                newUser.setId(id);
                newUser.setName((String) map.get("name"));
                newUser.setEmail((String) map.get("email"));
                newUser.setRole(Role.USER);
                newUser.setVotes(new HashSet<>());
                newUser.setInitiatives(new HashSet<>());
                userRepo.save(newUser);
                return newUser;
            });
            // костыль на первого админа, можно это заменить на консольное обращение к бд
            if (user.getEmail().equals("shelpyakov2d@gmail.com"))
                user.setRole(Role.ADMIN);
            userRepo.save(user);
            return user;
        };
    }
}
