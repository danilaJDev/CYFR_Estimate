package ae.cyfr.estimateapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.model.Work;
import ae.cyfr.estimateapp.model.User;
import ae.cyfr.estimateapp.model.EstimateGroup;
import ae.cyfr.estimateapp.repository.EstimateGroupRepository;
import ae.cyfr.estimateapp.repository.EstimateRepository;
import ae.cyfr.estimateapp.repository.UserRepository;
import ae.cyfr.estimateapp.service.EstimateService;
import ae.cyfr.estimateapp.service.SectionService;
import ae.cyfr.estimateapp.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EstimateController {

    @Autowired
    private WorkService workService;

    @Autowired
    private EstimateService estimateService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstimateRepository estimateRepository;

    @Autowired
    private EstimateGroupRepository estimateGroupRepository;

    @GetMapping("/")
    public String showEstimatePage(Model model) {
        model.addAttribute("sections", sectionService.getAllSections());
        return "estimate";
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportEstimateToExcel(
            @RequestParam(required = false) List<Long> workIds,
            @RequestParam(required = false) List<Double> quantities,
            @RequestParam(required = false) List<Double> coefficients,
            @RequestParam(required = false) Double totalCoefficient) throws IOException {

        List<Estimate> estimates = prepareEstimateData(workIds, quantities, coefficients, totalCoefficient);
        byte[] excelData = estimateService.createEstimateExcel(estimates);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy__HH.mm"));
        String filename = "Estimate__" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @PostMapping("/export-word")
    public ResponseEntity<byte[]> exportEstimateToWord(
            @RequestParam(required = false) List<Long> workIds,
            @RequestParam(required = false) List<Double> quantities,
            @RequestParam(required = false) List<Double> coefficients,
            @RequestParam(required = false) Double totalCoefficient) throws IOException {

        List<Estimate> estimates = prepareEstimateData(workIds, quantities, coefficients, totalCoefficient);
        byte[] wordData = estimateService.createEstimateWord(estimates);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy__HH.mm"));
        String filename = "Estimate__" + timestamp + ".docx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(wordData);
    }

    @PostMapping("/save-estimate")
    public String saveEstimate(
            @RequestParam(required = false) List<Long> workIds,
            @RequestParam(required = false) List<Double> quantities,
            @RequestParam(required = false) List<Double> coefficients,
            @RequestParam(required = false) Double totalCoefficient,
            Authentication authentication) {

        if (workIds == null || workIds.isEmpty()) {
            return "redirect:/";
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EstimateGroup estimateGroup = new EstimateGroup();
        estimateGroup.setUser(user);
        estimateGroup.setName("Смета от " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

        List<Estimate> estimates = prepareEstimateData(workIds, quantities, coefficients, totalCoefficient);
        for (Estimate estimate : estimates) {
            estimate.setEstimateGroup(estimateGroup);
        }
        estimateGroup.setEstimates(estimates);

        estimateGroupRepository.save(estimateGroup);

        return "redirect:/cabinet";
    }

    private List<Estimate> prepareEstimateData(List<Long> workIds, List<Double> quantities, List<Double> coefficients, Double totalCoefficient) {
        return estimateService.prepareEstimateData(workIds, quantities, coefficients, totalCoefficient);
    }
}
