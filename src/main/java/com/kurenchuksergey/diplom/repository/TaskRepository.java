package com.kurenchuksergey.diplom.repository;

import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskState;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("manager")
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserIdAndState(Long userId, TaskState state);
}
