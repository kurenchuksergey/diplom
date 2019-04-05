package com.kurenchuksergey.diplom.repository;

import com.kurenchuksergey.diplom.entity.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("manager")
public interface TaskRepository extends JpaRepository<Task, Long> {
}
