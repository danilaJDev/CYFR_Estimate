// package ae.cyfr.estimateapp.controller;

// import ae.cyfr.estimateapp.model.Work;
// import ae.cyfr.estimateapp.service.WorkService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// @Controller
// public class WorkController {

//     @Autowired
//     private WorkService workService;

//     @GetMapping("/works")
//     public String showWorksPage(Model model) {
//         model.addAttribute("works", workService.getAllWorks());
//         model.addAttribute("sections", workService.getAllSections());
//         return "works";
//     }

//     @PostMapping("/works/add")
//     public String addWork(@RequestParam String name,
//                           @RequestParam String unit,
//                           @RequestParam Double costPrice,
//                           @RequestParam Double clientPrice,
//                           @RequestParam Long sectionId) {
//         Work newWork = new Work();
//         // newWork.setName(name);
//         // newWork.setUnit(unit);
//         // newWork.setCostPrice(new java.math.BigDecimal(costPrice));
//         // newWork.setClientPrice(new java.math.BigDecimal(clientPrice));
//         // // newWork.setSection(workService.getSectionById(sectionId));
//         // // workService.saveWork(newWork);
//         return "redirect:/works";
//     }
// }
