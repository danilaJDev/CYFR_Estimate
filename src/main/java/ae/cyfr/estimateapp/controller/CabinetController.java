package ae.cyfr.estimateapp.controller;

import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.model.User;
import ae.cyfr.estimateapp.model.*;
import ae.cyfr.estimateapp.repository.EstimateGroupRepository;
import ae.cyfr.estimateapp.repository.EstimateRepository;
import ae.cyfr.estimateapp.repository.UserRepository;
import ae.cyfr.estimateapp.service.SectionService;
import ae.cyfr.estimateapp.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CabinetController {

    private final UserRepository userRepository;
    private final SectionService sectionService;
    private final WorkService workService;
    private final EstimateGroupRepository estimateGroupRepository;
    private final EstimateRepository estimateRepository;

    @Autowired
    public CabinetController(UserRepository userRepository, SectionService sectionService, WorkService workService, EstimateGroupRepository estimateGroupRepository, EstimateRepository estimateRepository) {
        this.userRepository = userRepository;
        this.sectionService = sectionService;
        this.workService = workService;
        this.estimateGroupRepository = estimateGroupRepository;
        this.estimateRepository = estimateRepository;
    }

    @GetMapping("/cabinet")
    public String cabinet(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        List<EstimateGroup> estimateGroups = estimateGroupRepository.findByUser(user);
        model.addAttribute("estimateGroups", estimateGroups);
        return "cabinet";
    }

    @GetMapping("/edit-estimate/{id}")
    public String showEditEstimatePage(@PathVariable Long id, Model model) {
        EstimateGroup estimateGroup = estimateGroupRepository.findById(id).orElse(null);
        if (estimateGroup == null) {
            return "redirect:/cabinet";
        }
        model.addAttribute("estimateGroup", estimateGroup);
        model.addAttribute("sections", sectionService.getAllSections());
        return "edit-estimate";
    }

    @PostMapping("/update-estimate/{id}")
    public String updateEstimate(@PathVariable Long id,
                                 @RequestParam(required = false) List<Long> workIds,
                                 @RequestParam(required = false) List<Double> quantities,
                                 @RequestParam(required = false) List<Double> coefficients,
                                 @RequestParam(required = false) Double totalCoefficient) {

        EstimateGroup existingEstimateGroup = estimateGroupRepository.findById(id).orElse(null);
        if (existingEstimateGroup == null) {
            return "redirect:/cabinet";
        }

        // This is a simple way to update. A more sophisticated approach would be to check for changes.
        estimateRepository.deleteAll(existingEstimateGroup.getEstimates());
        existingEstimateGroup.getEstimates().clear();

        List<Estimate> newEstimates = estimateService.prepareEstimateData(workIds, quantities, coefficients, totalCoefficient);

        for (Estimate item : newEstimates) {
            item.setEstimateGroup(existingEstimateGroup);
        }
        existingEstimateGroup.setEstimates(newEstimates);

        estimateGroupRepository.save(existingEstimateGroup);

        return "redirect:/cabinet";
    }
}