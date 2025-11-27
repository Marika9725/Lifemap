package com.lifemap.controller;

import com.lifemap.model.*;
import com.lifemap.model.projection.UserDTO;
import com.lifemap.service.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {
    private final UserService service;

    public HomeController(UserService service) {this.service = service;}

    @GetMapping
    public String home(@RequestParam(value = "lang", defaultValue = "pl") String lang, Model model) {
        setLanguage(lang, model);

        return "index";
    }

    private boolean isLanguageSupported(String lang) {
        return !lang.isEmpty() && (lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("en"));
    }

    private void setLanguage(String lang, Model model) {
        if (isLanguageSupported(lang)) model.addAttribute("lang", lang);
        else model.addAttribute("lang", "pl");
    }

    //region login
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "lang", defaultValue = "pl") String lang, Model model) {
        setLanguage(lang, model);
        model.addAttribute("user", new UserDTO());
        return "login";
    }
    //endregion

    //region register
    @GetMapping("/register")
    public String showRegisterForm(@RequestParam(value = "lang", defaultValue="pl") String lang, Model model) {
        setLanguage(lang, model);
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") @Valid UserDTO toSave,
            BindingResult result,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {

        var isRegistered = service.register(toSave, result, confirmPassword);

        if (result.hasErrors() || !isRegistered) {
            model.addAttribute("user", toSave);
            return "register";
        }

        return "redirect:/login";
    }
    //endregion
}
