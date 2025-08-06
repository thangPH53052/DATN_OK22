package org.example.datn.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/home")
    public String home() {
        return "view/home"; // trả về file home.html
    }

}
