package ae.cyfr.estimateapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.model.EstimateWrapper;
import ae.cyfr.estimateapp.service.EstimateService;
import ae.cyfr.estimateapp.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.stream.Collectors;

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
    public String showEstimatePage(Model model, @ModelAttribute("estimates") List<Estimate> estimates) {
        model.addAttribute("sections", workService.getAllSections());
        EstimateWrapper estimateWrapper = new EstimateWrapper();
        if (!estimates.isEmpty()) {
            estimateWrapper.setEstimates(estimates);
        }
        model.addAttribute("estimateWrapper", estimateWrapper);
        return "estimate";
    }

    @PostMapping("/estimate/update")
    public String updateEstimate(@Valid @ModelAttribute EstimateWrapper estimateWrapper,
                                 BindingResult bindingResult,
                                 @ModelAttribute("estimates") List<Estimate> estimates,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sections", workService.getAllSections());
            return "estimate";
        }

        estimates.clear();
        List<Estimate> selectedEstimates = estimateWrapper.getEstimates().stream()
                .filter(Estimate::isSelected)
                .collect(Collectors.toList());

        for (Estimate item : selectedEstimates) {
            item.setWork(workService.getWorkById(item.getWork().getId()));
            double total = item.getQuantity() * item.getWork().getClientPrice().doubleValue() * item.getCoefficient();
            item.setTotal(Math.round(total * 100.0) / 100.0);
            estimates.add(item);
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
