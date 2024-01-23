package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.payload.LoginDTO;
import org.example.payload.RegisterDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("dto", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("dto") RegisterDTO registerDTO,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        if (!Objects.equals(registerDTO.getPassword(), registerDTO.getPrePassword())) {
            model.addAttribute("passErrMess", "Passwords are not equals");
            return "auth/register";
        }

        Optional<User> byEmail = userDAO.getByEmail(registerDTO.getEmail());
        if (byEmail.isPresent())
            return "redirect:/auth/register";

        Optional<User> savedUser = userDAO.save(User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build());

        System.out.println("saved User " + savedUser);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "err", required = false) String err,
                            Model model) {
        model.addAttribute("err", err);
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "auth/logout";
    }

}
