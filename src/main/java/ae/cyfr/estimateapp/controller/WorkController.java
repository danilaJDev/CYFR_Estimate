package ae.cyfr.estimateapp.controller;

import ae.cyfr.estimateapp.model.Section;
import ae.cyfr.estimateapp.model.Subsection;
import ae.cyfr.estimateapp.model.Work;
import ae.cyfr.estimateapp.service.SectionService;
import ae.cyfr.estimateapp.service.SubsectionService;
import ae.cyfr.estimateapp.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WorkController {

    @Autowired
    private WorkService workService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SubsectionService subsectionService;

    @GetMapping("/works")
    public String showWorksPage(Model model) {
        model.addAttribute("sections", sectionService.getAllSections());
        model.addAttribute("newSection", new Section());
        model.addAttribute("newSubsection", new Section());
        model.addAttribute("newWork", new Work());
        return "works";
    }

    @PostMapping("/sections")
    public String addSection(@ModelAttribute Section newSection) {
        sectionService.saveSection(newSection);
        return "redirect:/works";
    }

    @PostMapping("/subsections")
    public String addSubsection(@ModelAttribute Subsection newSubsection) {
        subsectionService.saveSubsection(newSubsection);
        return "redirect:/works";
    }

    @PostMapping("/works")
    public String addWork(@ModelAttribute Work newWork, @RequestParam Long subsectionId) {
        Subsection subsection = subsectionService.getSubsectionById(subsectionId);
        newWork.setSubsection(subsection);
        workService.saveWork(newWork);
        return "redirect:/works";
    }

    @GetMapping("/works/delete/{id}")
    public String deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return "redirect:/works";
    }

    @GetMapping("/sections/delete/{id}")
    public String deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return "redirect:/works";
    }

    @GetMapping("/subsections/delete/{id}")
    public String deleteSubsection(@PathVariable Long id) {
        subsectionService.deleteSubsection(id);
        return "redirect:/works";
    }
}
