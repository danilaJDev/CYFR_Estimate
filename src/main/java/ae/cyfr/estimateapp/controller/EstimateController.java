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
import ae.cyfr.estimateapp.service.SectionService;
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

    @Autowired
    private SectionService sectionService;

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

    private List<Estimate> prepareEstimateData(List<Long> workIds, List<Double> quantities, List<Double> coefficients, Double totalCoefficient) {
        List<Estimate> estimates = new ArrayList<>();
        if (workIds == null || workIds.isEmpty()) {
            return estimates;
        }

        final double finalTotalCoefficient = (totalCoefficient != null && totalCoefficient > 0) ? totalCoefficient : 1.0;

        Map<Long, Work> workMap = workService.getWorksByIds(workIds).stream()
                .collect(Collectors.toMap(Work::getId, w -> w));

        for (int i = 0; i < workIds.size(); i++) {
            Long currentWorkId = workIds.get(i);
            Work work = workMap.get(currentWorkId);
            if (work != null) {
                Estimate item = new Estimate();
                item.setWork(work);

                double quantity = (quantities != null && i < quantities.size()) ? quantities.get(i) : 1.0;
                item.setQuantity(quantity);

                double baseCoefficient = (coefficients != null && i < coefficients.size()) ? coefficients.get(i) : 1.0;
                double effectiveCoefficient = baseCoefficient * finalTotalCoefficient;
                item.setCoefficient(effectiveCoefficient);

                item.setTotalCost(item.getQuantity() * item.getCoefficient() * work.getClientPrice());
                estimates.add(item);
            }
        }
        return estimates;
    }
}
