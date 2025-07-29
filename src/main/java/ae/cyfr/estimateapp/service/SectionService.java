package ae.cyfr.estimateapp.service;

import java.util.List;
import ae.cyfr.estimateapp.model.Section;
import ae.cyfr.estimateapp.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id).orElse(null);
    }

    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }
}
