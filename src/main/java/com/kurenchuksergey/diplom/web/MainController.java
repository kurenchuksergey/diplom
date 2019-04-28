package com.kurenchuksergey.diplom.web;

import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskState;
import com.kurenchuksergey.diplom.entity.TaskType;
import com.kurenchuksergey.diplom.entity.UserIdent;
import com.kurenchuksergey.diplom.repository.TaskRepository;
import com.kurenchuksergey.diplom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@Profile("manager")
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/upload/picture")
    public ModelAndView uploadPicture() {
        ModelAndView mav = new ModelAndView("uploadForm");
        UserIdent curUser = userService.getCurUser();
        //refresh relationship
        curUser = userService.getRepository().findById(curUser.getId()).get();
        mav.addObject("user", curUser);
        mav.addObject("type", TaskType.values());
        return mav;
    }

    @GetMapping("/")
    public ModelAndView mainPage() {
        ModelAndView main = new ModelAndView("main");
        UserIdent curUser = userService.getCurUser();
        main.addObject("user", curUser);
        return main;
    }

    @GetMapping("/view")
    public ModelAndView viewPage() {
        ModelAndView view = new ModelAndView("view");
        UserIdent curUser = userService.getCurUser();
        //refresh relationship
        view.addObject("user", curUser);
        List<Task> doneTask = taskRepository.findAllByUserIdAndState(curUser.getId(), TaskState.DONE);
        view.addObject("task", doneTask);
        return view;
    }
}
