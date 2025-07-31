package ae.cyfr.estimateapp.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
        model.addAttribute("newSubsection", new Subsection());
        model.addAttribute("newWork", new Work());
        return "works";
    }

    @GetMapping("/subsections/{sectionId}")
    @ResponseBody
    public List<Subsection> getSubsectionsBySection(@PathVariable Long sectionId) {
        return subsectionService.getAllSubsectionBySectionId(sectionId);
    }

    // --- CRUD операции для Разделов (Sections) ---

    @PostMapping("/sections")
    public String addSection(@ModelAttribute Section newSection) {
        sectionService.saveSection(newSection);
        return "redirect:/works";
    }

    @PostMapping("/sections/update/{id}")
    public String updateSection(@PathVariable Long id, @ModelAttribute Section sectionDetails) {
        Section section = sectionService.getSectionById(id);
        if (section != null) {
            section.setName(sectionDetails.getName());
            sectionService.saveSection(section);
        }
        return "redirect:/works";
    }

    @GetMapping("/sections/delete/{id}")
    public String deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return "redirect:/works";
    }

    // --- CRUD операции для Подразделов (Subsections) ---

    @PostMapping("/subsections")
    public String addSubsection(@ModelAttribute Subsection newSubsection, @RequestParam Long sectionId) {
        Section section = sectionService.getSectionById(sectionId);
        if (section != null) {
            newSubsection.setSection(section);
            subsectionService.saveSubsection(newSubsection);
        }
        return "redirect:/works";
    }

    @PostMapping("/subsections/update/{id}")
    public String updateSubsection(@PathVariable Long id, @ModelAttribute Subsection subsectionDetails) {
        Subsection subsection = subsectionService.getSubsectionById(id);
        if (subsection != null) {
            subsection.setName(subsectionDetails.getName());
            subsectionService.saveSubsection(subsection);
        }
        return "redirect:/works";
    }

    @GetMapping("/subsections/delete/{id}")
    public String deleteSubsection(@PathVariable Long id) {
        subsectionService.deleteSubsection(id);
        return "redirect:/works";
    }

    // --- CRUD операции для Работ (Works) ---

    @PostMapping("/works")
    public String addWork(@ModelAttribute Work newWork, @RequestParam Long subsectionId) {
        Subsection subsection = subsectionService.getSubsectionById(subsectionId);
        if (subsection != null) {
            newWork.setSubsection(subsection);
            workService.saveWork(newWork);
        }
        return "redirect:/works";
    }

    @PostMapping("/works/update/{id}")
    public String updateWork(@PathVariable Long id, @ModelAttribute Work workDetails) {
        Work work = workService.getWorkById(id);
        if (work != null) {
            work.setName(workDetails.getName());
            work.setUnit(workDetails.getUnit());
            work.setCostPrice(workDetails.getCostPrice());
            work.setClientPrice(workDetails.getClientPrice());
            workService.saveWork(work);
        }
        return "redirect:/works";
    }

    @GetMapping("/works/delete/{id}")
    public String deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return "redirect:/works";
    }
}
