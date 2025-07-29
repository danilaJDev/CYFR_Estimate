package ae.cyfr.estimateapp.service;

import java.util.List;
import ae.cyfr.estimateapp.model.Section;
import ae.cyfr.estimateapp.model.Work;
import ae.cyfr.estimateapp.repository.SectionRepository;
import ae.cyfr.estimateapp.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkService {

    @Autowired
    private WorkRepository workRepository;

    public List<Work> getAllWorks() {
        return workRepository.findAll();
    }

    public Work saveWork(Work work) {
        return workRepository.save(work);
    }

    public void deleteWork(Long id) {
        workRepository.deleteById(id);
    }

    public Work getWorkById(Long id) {
        return workRepository.findById(id).orElse(null);
    }

    public List<Work> getWorksByIds(List<Long> ids) {
        return workRepository.findAllById(ids);
    }
}
