package com.kurenchuksergey.diplom.repository;

import com.kurenchuksergey.diplom.entity.UserIdent;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Profile("manager")
public interface UserIdentRepository extends JpaRepository<UserIdent , Long> {
    Optional<UserIdent> findFirstBySub(String sub);
}
