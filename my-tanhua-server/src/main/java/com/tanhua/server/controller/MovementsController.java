package com.tanhua.server.controller;

import com.tanhua.server.service.MovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("movements")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    /**
     *@RequestParam 参数名能对上就不用了加 这个注解
     *
     */
    @PostMapping
    public ResponseEntity savePublish(@RequestParam(value = "textContent", required = false) String textContent,
                                      @RequestParam(value = "location", required = false) String location,
                                      @RequestParam("longitude") String longitude,
                                      @RequestParam("latitude") String latitude,
                                      @RequestParam(value = "imageContent", required = false) MultipartFile[] multipartFile,
                                      @RequestHeader("Authorization") String token) {


        try {
            boolean b = this.movementsService.savePublish(textContent, location, longitude, latitude, multipartFile, token);
            if (b){
               return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }




}