package ae.cyfr.estimateapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.service.EstimateService;
import ae.cyfr.estimateapp.service.WorkService;
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

    @GetMapping("/estimate")
    public String showEstimatePage(Model model) {
        model.addAttribute("sections", workService.getAllSections());
        return "estimate";
    }

    @PostMapping("/estimate/update")
    public String updateEstimate(@RequestParam(required = false) List<Long> selectedWorkIds,
                                 @RequestParam(required = false) List<Long> workIds,
                                 @RequestParam(required = false) List<Double> quantities,
                                 @RequestParam(required = false) List<Double> coefficients,
                                 @ModelAttribute("estimates") List<Estimate> estimates) {

        estimates.clear();

        if (selectedWorkIds == null || selectedWorkIds.isEmpty()) {
            return "redirect:/estimate";
        }

        // Используем отдельный индекс для actualData
        int actualIndex = 0;
        for (int i = 0; i < workIds.size(); i++) {
            Long currentWorkId = workIds.get(i);
            if (selectedWorkIds.contains(currentWorkId)) {
                Estimate item = new Estimate();
                item.setWork(workService.getWorkById(currentWorkId));
                item.setQuantity(quantities.get(actualIndex));
                item.setCoefficient(coefficients.get(actualIndex));
                item.setTotalCost(item.getQuantity() * item.getCoefficient() * item.getWork().getClientPrice());
                estimates.add(item);
                actualIndex++;
            }
        }

        return "redirect:/estimate";
    }

    @GetMapping("/estimate/export")
    public ResponseEntity<byte[]> exportEstimate(@ModelAttribute("estimates") List<Estimate> estimates) throws IOException {
        byte[] excelData = estimateService.createEstimateExcel(estimates);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "estimate.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
