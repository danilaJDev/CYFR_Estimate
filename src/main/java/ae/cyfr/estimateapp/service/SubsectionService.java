package ae.cyfr.estimateapp.service;

import java.util.List;
import ae.cyfr.estimateapp.model.Section;
import ae.cyfr.estimateapp.model.Subsection;
import ae.cyfr.estimateapp.repository.SubsectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubsectionService {

    @Autowired
    private SubsectionRepository subsectionRepository;

    public List<Subsection> getAllSubsections() {
        return subsectionRepository.findAll();
    }

    public Subsection saveSubsection(Subsection subsection) {
        return subsectionRepository.save(subsection);
    }

    public Subsection getSubsectionById(Long subsectionId) {
        return subsectionRepository.findById(subsectionId).orElse(null);
    }

    public void deleteSubsection(Long subsectionId) {
        subsectionRepository.deleteById(subsectionId);
    }
}
