package com.kurenchuksergey.diplom.config.security;

import com.kurenchuksergey.diplom.entity.UserIdent;
import com.kurenchuksergey.diplom.repository.UserIdentRepository;
import com.kurenchuksergey.diplom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@Profile("manager")

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserIdentRepository userIdentRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().mvcMatchers("/home","/login", "/health","/task/**","/resources/**").permitAll()
                .anyRequest().authenticated().and().logout().logoutSuccessUrl("/home").permitAll().and().csrf().disable();
    }

    @Bean
    @Order(0)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
    @Bean
    public PrincipalExtractor principalExtractor(UserService userIdentRepository) {
        return (map) -> {
            String id = (String) map.get("sub");

            UserIdent user = userIdentRepository.getRepository().findFirstBySub(id).orElseGet(() -> {
                String email = (String) map.get("email");
                String pic = (String) map.get("picture");
                String local = (String) map.get("locale");
                String name = (String) map.get("name");

                UserIdent userIdent = new UserIdent();
                userIdent.setEmail(email);
                userIdent.setUserPic(pic);
                userIdent.setName(name);
                userIdent.setSub(id);
                userIdent.setLocale(local);
                return userIdent;
            });
          return userIdentRepository.save(user);
        };
    }
}
