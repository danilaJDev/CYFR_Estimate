package ae.cyfr.estimateapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ae.cyfr.estimateapp.model.EstimateItem;
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
@SessionAttributes("estimateItems")
public class EstimateController {

    @Autowired
    private WorkService workService;

    @Autowired
    private EstimateService estimateService;

    @ModelAttribute("estimateItems")
    public List<EstimateItem> getEstimateItems() {
        return new ArrayList<>();
    }

    @GetMapping("/estimate")
    public String showEstimatePage(Model model) {
        model.addAttribute("sections", workService.getAllSections());
        return "estimate";
    }

    @PostMapping("/estimate/add")
    public String addEstimateItem(@RequestParam Long workId, @RequestParam Double quantity, @RequestParam(defaultValue = "1.0") Double coefficient, @ModelAttribute("estimateItems") List<EstimateItem> estimateItems) {
        EstimateItem item = new EstimateItem();
        item.setWork(workService.getAllWorks().stream().filter(w -> w.getId().equals(workId)).findFirst().orElse(null));
        item.setQuantity(quantity);
        item.setCoefficient(coefficient);
        estimateItems.add(item);
        return "redirect:/estimate";
    }

    @GetMapping("/estimate/export")
    public ResponseEntity<byte[]> exportEstimate(@ModelAttribute("estimateItems") List<EstimateItem> estimateItems) throws IOException {
        byte[] excelData = estimateService.createEstimateExcel(estimateItems);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "estimate.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
