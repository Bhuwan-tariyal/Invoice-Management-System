package com.tariyal.controller;


import com.tariyal.dto.DashBoardResponse;
import com.tariyal.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

   @Autowired
   private DashBoardService dashBoardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        DashBoardResponse dashBoardResponse = dashBoardService.getDashBoardDate(authentication);
        return ResponseEntity.ok(dashBoardResponse);
    }

}
