package ae.cyfr.estimateapp.controller;

import ae.cyfr.estimateapp.model.Role;
import ae.cyfr.estimateapp.model.User;
import ae.cyfr.estimateapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final UserRepository userRepository;

    @Autowired
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        List<User> estimators = userRepository.findByRoleWithEstimates(Role.ESTIMATOR);
        model.addAttribute("estimators", estimators);
        return "admin";
    }
}