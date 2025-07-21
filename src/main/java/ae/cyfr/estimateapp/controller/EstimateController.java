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
    public List<Estimate> getEstimateItems() {
        return new ArrayList<>();
    }

    @GetMapping("/estimate")
    public String showEstimatePage(Model model) {
        model.addAttribute("sections", workService.getAllSections());
        return "estimate";
    }

    @PostMapping("/estimate/update")
    public String updateEstimate(@RequestParam(required = false) List<Long> selectedWorks,
                                 @RequestParam(required = false) List<Double> quantity,
                                 @RequestParam(required = false) List<Double> coefficient,
                                 @ModelAttribute("estimateItems") List<Estimate> estimateItems) {
        estimateItems.clear();
        if (selectedWorks != null) {
            for (int i = 0; i < selectedWorks.size(); i++) {
                Estimate item = new Estimate();
                item.setWork(workService.getWorkById(selectedWorks.get(i)));
                item.setQuantity(quantity.get(i));
                item.setCoefficient(coefficient.get(i));
                estimateItems.add(item);
            }
        }
        return "redirect:/estimate";
    }

    @GetMapping("/estimate/export")
    public ResponseEntity<byte[]> exportEstimate(@ModelAttribute("estimateItems") List<Estimate> estimates) throws IOException {
        byte[] excelData = estimateService.createEstimateExcel(estimates);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "estimate.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
