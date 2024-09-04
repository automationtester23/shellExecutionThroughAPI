package com.sumedh.linuxapi.api;
//changes done ++8
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.sumedh.linuxapi.domain.shellCommandExecutor;
import com.sumedh.linuxapi.ShellRequest.ShellCommandRequest;

@RestController
public class LinuxControllers {
    @Autowired
    private shellCommandExecutor commandExecutor;

    // sample endpoint :http://localhost:8080/executeCommand?name=cd;pwd;ls -lrth
    @GetMapping("/executeCommand")
    public String hello(@RequestParam(value = "name", defaultValue = "ls") String name) {
        return commandExecutor.executeCommand(name);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/executeRemoteShellCommand")
    public String executeCommand(@RequestBody ShellCommandRequest commandRequest) {
        String command = commandRequest.getCommand();
        String hostname = commandRequest.getHostname();
        String username = commandRequest.getUsername();
        int port = commandRequest.getPort();
        String password = commandRequest.getPassword();

        // Execute the command using the established SSH connection
        String result = commandExecutor.executeCommand(command, hostname, port, username, password);

        return result;
    }
    // curl request for above Endpoint
    /*
     * curl -v -X POST -H "Content-Type: application/json" -d '{
     * "command": "ls",
     * "hostname": "",
     * "username": "",
     * "port": 22,
     * "password": ""
     * }' http://localhost:8080/executeRemoteShellCommand
     */
}
