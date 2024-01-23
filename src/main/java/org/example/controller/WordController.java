package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dao.AttachmentDAO;
import org.example.dao.CategoryDAO;
import org.example.dao.WordDAO;
import org.example.model.Attachment;
import org.example.model.Category;
import org.example.model.User;
import org.example.model.Word;
import org.example.payload.AddCategoryDTO;
import org.example.payload.AddWordDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/word")
@RequiredArgsConstructor
public class WordController {
    private final WordDAO wordDAO;
    private final CategoryDAO categoryDAO;
    private final AttachmentDAO attachmentDAO;
    private final String path = "D:\\project\\idea\\pdp\\java\\spring\\core\\app-dictionary-spring-core\\src\\main\\resources\\static";
    @GetMapping("/by-category-id/{categoryId}")
    public String categoryPage(Model model, @PathVariable("categoryId") UUID categoryId) {
        List<Word> words = wordDAO.getAllByCategoryId(categoryId);
        model.addAttribute("words", words);
        return "word";
    }

    @PostMapping("/by-category-id/{categoryId}")
    public String addWord(@Valid @ModelAttribute AddWordDTO addWordDTO,
                          @PathVariable("categoryId") UUID categoryId) throws IOException {

        String on = addWordDTO.image().getOriginalFilename();
        assert on != null;
        String url = "/img/" + UUID.randomUUID() + on.substring(on.lastIndexOf('.'));


        Files.copy(addWordDTO.image().getInputStream(), Path.of(path + url), StandardCopyOption.REPLACE_EXISTING);

        Attachment build = Attachment.builder()
                .name(addWordDTO.image().getOriginalFilename())
                .url(url)
                .size(addWordDTO.image().getSize())
                .contentType(addWordDTO.image().getContentType())
                .build();

        Optional<Attachment> savedAttachment = attachmentDAO.save(build);


        Optional<Category> optionalCategory = categoryDAO.getById(categoryId);

        Word word = Word.builder()
                .name(addWordDTO.name())
                .translate(addWordDTO.translate())
                .category(optionalCategory.get())
                .attachment(savedAttachment.get())
                .build();

        System.out.println(wordDAO.save(word));

        return "redirect:/word/by-category-id/" + categoryId;
    }

}
