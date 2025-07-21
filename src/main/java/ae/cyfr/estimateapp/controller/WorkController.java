package ae.cyfr.estimateapp.controller;

import ae.cyfr.estimateapp.model.Section;
import ae.cyfr.estimateapp.model.Work;
import ae.cyfr.estimateapp.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WorkController {

    @Autowired
    private WorkService workService;

    @GetMapping("/works")
    public String showWorksPage(Model model) {
        model.addAttribute("sections", workService.getAllSections());
        model.addAttribute("newSection", new Section());
        model.addAttribute("newWork", new Work());
        return "works";
    }

    @PostMapping("/sections")
    public String addSection(@ModelAttribute Section newSection) {
        workService.saveSection(newSection);
        return "redirect:/works";
    }

    @PostMapping("/works")
    public String addWork(@ModelAttribute Work newWork, @RequestParam Long sectionId) {
        Section section = workService.getSectionById(sectionId);
        newWork.setSection(section);
        workService.saveWork(newWork);
        return "redirect:/works";
    }
}
