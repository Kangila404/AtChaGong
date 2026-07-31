package org.example.server.user.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe(){
        
        return ResponseEntity.ok(new UserMeResponse("서버 살아있음요!"));
    }


}
