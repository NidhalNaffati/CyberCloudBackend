package tn.esprit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.service.BadWordsService;

@RestController
@RequestMapping("/api/badwords")
public class BadWordsController {

    @Autowired
    private BadWordsService badWordsService;

    @PostMapping("/check")
    public String checkForBadWords(@RequestBody String content) {
        return badWordsService.checkForBadWords(content);
    }
}