package com.kurenchuksergey.diplom.web;

import com.kurenchuksergey.diplom.entity.UserIdent;
import com.kurenchuksergey.diplom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Profile("manager")
public class TestController {
    @Autowired
    private UserService userService;
//
//    @Autowired
//    private ForeignWorkerClient foreignWorkerClient;
//
//    @GetMapping("/test/{id}")
//    public Task getById(@PathVariable long id){
//       return foreignWorkerClient.getById(id);
//    }

    @GetMapping("/upload/picture")
    public ModelAndView uploadPicture() {
        ModelAndView mav = new ModelAndView("uploadForm");
        UserIdent curUser = userService.getCurUser();
        //refresh relationship
        curUser = userService.getRepository().findById(curUser.getId()).get();
        mav.addObject("user", curUser);
        return mav;
    }

    @GetMapping("/home")
    public ModelAndView mainPage() {
        ModelAndView main = new ModelAndView("main");
        UserIdent curUser = userService.getCurUser();
        main.addObject("user", curUser);
        return main;
    }
    @GetMapping("/del")
    public String DellButtonResourse(){
        return "del.png";
    }
}
