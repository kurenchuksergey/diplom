package com.kurenchuksergey.diplom.web;

import com.kurenchuksergey.diplom.config.manager.TaskManagerOutChannelConfiguration;
import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskState;
import com.kurenchuksergey.diplom.entity.TaskType;
import com.kurenchuksergey.diplom.repository.TaskRepository;
import com.kurenchuksergey.diplom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("task")
@Profile("manager")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskManagerOutChannelConfiguration.TaskGateway taskGateway;

    @PostMapping(value = "/{type}")
    public HttpStatus createTask(@RequestParam(name = "file") MultipartFile file, @PathVariable(name = "type") String type) throws IOException {
        if (file == null || file.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }
        Task task = new Task();
        task.setFileName(file.getOriginalFilename());
        task.setImageContentType(file.getContentType());
        task.setImage(file.getBytes());
        task.setState(TaskState.WAIT);
        task.setUserId(userService.getCurUser().getId());
        task.setType(TaskType.valueOf(type));
        task = taskRepository.save(task);
        if (task.getId() == null) {
            return HttpStatus.BAD_GATEWAY;
        }
//        Task task1 = taskGateway.sendToRabbit(task);
        return HttpStatus.OK;
    }

    @DeleteMapping("{id}")
    public void deleteTask(@PathVariable long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping("{id}")
    public Task getTask(@PathVariable long id) throws ChangeSetPersister.NotFoundException {
        return taskRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    /* TODO исключение и пермишины*/
    @PutMapping("{id}")
    public Task updateTask(@RequestBody Task task, @PathVariable Long id) {
        if (task == null || task.getImage() == null || task.getImage().length == 0) {
            throw new RuntimeException();
        }
        if (task.getId() == null && id != null) {
            task.setId(id);
        }
        return taskRepository.save(task);
    }

    @GetMapping("{id}/image")
    @ResponseBody
    ResponseEntity<byte[]> getImageByTaskId(@PathVariable("id") long taskId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            Task task = taskRepository.getOne(taskId);
            MediaType mediaType = MediaType.parseMediaType(task.getImageContentType());
            httpHeaders.setContentType(mediaType);
            return new ResponseEntity<>(task.getImage(), httpHeaders, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/image/prev")
    ResponseEntity<byte[]> getImagePrevByTaskId(@PathVariable("id") long taskId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            Task task = taskRepository.getOne(taskId);
            MediaType mediaType = MediaType.parseMediaType(task.getImageContentType());
            httpHeaders.setContentType(mediaType);
            return new ResponseEntity<>(task.getPrevImage(), httpHeaders, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler
    private void handleException(Exception e) {
        e.printStackTrace();
    }

}
