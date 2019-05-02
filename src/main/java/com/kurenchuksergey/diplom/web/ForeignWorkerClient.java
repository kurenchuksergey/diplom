package com.kurenchuksergey.diplom.web;

import com.kurenchuksergey.diplom.entity.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(serviceId = "manager")
@Profile("worker")
public interface ForeignWorkerClient {

    @RequestMapping(method = RequestMethod.GET, value = "task/{id}")
    Task getById(@PathVariable("id") long id);

    @RequestMapping(method = RequestMethod.PUT, path = "task/{id}")
    Task updateTask(@PathVariable("id") long id, @RequestBody Task task);

    @RequestMapping(method = RequestMethod.GET, path = "task/{id}/image")
    byte[] getImageByTaskId(@PathVariable("id") long id);
}
