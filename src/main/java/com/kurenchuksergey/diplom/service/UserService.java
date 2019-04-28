package com.kurenchuksergey.diplom.service;

import com.kurenchuksergey.diplom.entity.UserIdent;
import com.kurenchuksergey.diplom.repository.UserIdentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Profile("manager")
public class UserService {
    @Autowired
    private UserIdentRepository userIdentRepository;

    public UserIdentRepository getRepository() {
        return this.userIdentRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserIdent save(UserIdent userIdent) {
        return userIdentRepository.saveAndFlush(userIdent);
    }

    public UserIdent getCurUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserIdent) {
            return (UserIdent) principal;
        }
        return null;
    }
}
