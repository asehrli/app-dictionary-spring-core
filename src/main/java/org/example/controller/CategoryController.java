package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dao.CategoryDAO;
import org.example.model.Category;
import org.example.model.User;
import org.example.payload.AddCategoryDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryDAO categoryDAO;
    @GetMapping
    public String categoryPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("dto", new AddCategoryDTO());
        List<Category> allByUserId = categoryDAO.getAllByUserId(user.getId());
        model.addAttribute("categories", allByUserId);
        return "category";
    }

    @PostMapping
    public String addCategory(@Valid @ModelAttribute("dto") AddCategoryDTO addCategoryDTO,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal User user,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "category";
        }

        boolean exists = categoryDAO.exists(addCategoryDTO.getName(), user.getId());
        if (exists) {
            model.addAttribute("err", "Category already exists");
            return "category";
        }

        Optional<Category> saved = categoryDAO.save(Category.builder()
                .name(addCategoryDTO.getName())
                .user(user)
                .build());

        System.out.println(saved);

        return "redirect:/category";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") UUID id) {
        categoryDAO.delete(id);
        return "redirect:/category";
    }

}
