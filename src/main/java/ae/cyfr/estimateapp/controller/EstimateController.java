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
import ae.cyfr.estimateapp.service.EstimateService;
import ae.cyfr.estimateapp.service.WorkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("estimates")
public class EstimateController {

    @Autowired
    private WorkService workService;

    @Autowired
    private EstimateService estimateService;

    @ModelAttribute("estimates")
    public List<Estimate> getEstimates() {
        return new ArrayList<>();
    }

    @GetMapping("/")
    public String showEstimatePage(Model model, HttpSession session) {
        model.addAttribute("sections", workService.getAllSections());
        List<Estimate> estimates = (List<Estimate>) session.getAttribute("estimates");
        if (estimates != null) {
            double total = estimates.stream().mapToDouble(Estimate::getTotalCost).sum();
            model.addAttribute("totalSum", total);
        }
        return "estimate";
    }

    @PostMapping("/update")
    public String updateEstimate(@RequestParam(required = false) List<Long> selectedWorkIds,
                                 @RequestParam(required = false) List<Long> workIds,
                                 @RequestParam(required = false) List<Double> quantities,
                                 @RequestParam(required = false) List<Double> coefficients,
                                 @ModelAttribute("estimates") List<Estimate> estimates) {

        estimates.clear();

        if (selectedWorkIds == null || selectedWorkIds.isEmpty()) {
            return "redirect:/";
        }

        List<Work> works = workService.getWorksByIds(selectedWorkIds);

        Map<Long, Work> workMap = works.stream()
                .collect(Collectors.toMap(Work::getId, w -> w));

        int actualIndex = 0;
        for (int i = 0; i < workIds.size(); i++) {
            Long currentWorkId = workIds.get(i);
            if (selectedWorkIds.contains(currentWorkId)) {
                Work work = workMap.get(currentWorkId);
                if (work != null) {
                    Estimate item = new Estimate();
                    item.setWork(work);
                    item.setQuantity(quantities.get(actualIndex));
                    item.setCoefficient(coefficients.get(actualIndex));
                    item.setTotalCost(item.getQuantity() * item.getCoefficient() * work.getClientPrice());
                    estimates.add(item);
                }
                actualIndex++;
            }
        }

        return "redirect:/";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEstimate(@ModelAttribute("estimates") List<Estimate> estimates) throws IOException {
        byte[] excelData = estimateService.createEstimateExcel(estimates);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy__HH.mm");
        String timestamp = now.format(formatter);
        String filename = "Estimate__" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
