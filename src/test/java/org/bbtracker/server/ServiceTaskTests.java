package org.bbtracker.server;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.kickmyb.server.ServerApplication;
import org.kickmyb.server.account.BadCredentialsException;
import org.kickmyb.server.account.MUser;
import org.kickmyb.server.account.MUserRepository;
import org.kickmyb.server.account.ServiceAccount;
import org.kickmyb.server.task.MTask;
import org.kickmyb.server.task.MTaskRepository;
import org.kickmyb.server.task.ServiceTask;
import org.kickmyb.transfer.AddTaskRequest;
import org.kickmyb.transfer.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// TODO pour celui ci on aimerait pouvoir mocker l'utilisateur pour ne pas avoir à le créer

// https://reflectoring.io/spring-boot-mock/#:~:text=This%20is%20easily%20done%20by,our%20controller%20can%20use%20it.

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ServerApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class ServiceTaskTests {

    @Autowired
    private ServiceAccount serviceAccount;
    @Autowired
    private ServiceTask service;

    @Autowired
    private MUserRepository userRepository;
    @Autowired
    private MTaskRepository taskRepository;

    MUser createUser() {
        SignupRequest request = new SignupRequest();
        request.username = "test";
        request.password = "test";
        try {
            serviceAccount.signup(request);
            return userRepository.findByUsername("test").get();
        } catch (BadCredentialsException | ServiceAccount.UsernameTooShort | ServiceAccount.PasswordTooShort |
                 ServiceAccount.UsernameAlreadyTaken e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddTaskOk() throws ServiceTask.Empty, ServiceTask.TooShort, ServiceTask.Existing {
        MUser user = createUser();
        AddTaskRequest req = new AddTaskRequest();
        req.name = "testy";
        service.addOne(req, user);

        List<MTask> tasks = StreamSupport.stream(taskRepository.findAll().spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(tasks.size(), 1);
        Assert.assertEquals(tasks.get(0).name, "testy");
    }

    @Test
    void testAddTaskTooShort() {
        MUser user = createUser();
        Assertions.assertThrows(ServiceTask.TooShort.class, () -> {
            AddTaskRequest req = new AddTaskRequest();
            req.name = "t";
            service.addOne(req, user);
        }, "TooShort was expected");
    }

    @Test
    void testAddTaskDuplicateName() {
        MUser user = createUser();
        Assertions.assertThrows(ServiceTask.Existing.class, () -> {
            AddTaskRequest req = new AddTaskRequest();
            req.name = "test";
            service.addOne(req, user);
            service.addOne(req, user);
        }, "Existing was expected");
    }

}
