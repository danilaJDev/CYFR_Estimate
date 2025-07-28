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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/")
    public String showEstimatePage(Model model) {
        model.addAttribute("sections", workService.getAllSections());
        return "estimate";
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportEstimateToExcel(
            @RequestParam(required = false) List<Long> workIds,
            @RequestParam(required = false) List<Double> quantities,
            @RequestParam(required = false) List<Double> coefficients) throws IOException {

        List<Estimate> estimates = prepareEstimateData(workIds, quantities, coefficients);
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
            @RequestParam(required = false) List<Double> coefficients) throws IOException {

        List<Estimate> estimates = prepareEstimateData(workIds, quantities, coefficients);
        byte[] wordData = estimateService.createEstimateWord(estimates);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy__HH.mm"));
        String filename = "Estimate__" + timestamp + ".docx";

        HttpHeaders headers = new HttpHeaders();
        // Set correct content type for .docx files
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(wordData);
    }

    private List<Estimate> prepareEstimateData(List<Long> workIds, List<Double> quantities, List<Double> coefficients) {
        List<Estimate> estimates = new ArrayList<>();
        if (workIds == null || workIds.isEmpty()) {
            return estimates;
        }

        Map<Long, Work> workMap = workService.getWorksByIds(workIds).stream()
                .collect(Collectors.toMap(Work::getId, w -> w));

        for (int i = 0; i < workIds.size(); i++) {
            Long currentWorkId = workIds.get(i);
            Work work = workMap.get(currentWorkId);
            if (work != null) {
                Estimate item = new Estimate();
                item.setWork(work);
                if (quantities != null && i < quantities.size()) {
                    item.setQuantity(quantities.get(i));
                }
                if (coefficients != null && i < coefficients.size()) {
                    item.setCoefficient(coefficients.get(i));
                }
                item.setTotalCost(item.getQuantity() * item.getCoefficient() * work.getClientPrice());
                estimates.add(item);
            }
        }
        return estimates;
    }
}
