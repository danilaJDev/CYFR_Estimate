package com.example.estimateapp.service;

import com.example.estimateapp.model.Section;
import com.example.estimateapp.model.Work;
import com.example.estimateapp.repository.SectionRepository;
import com.example.estimateapp.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private WorkRepository workRepository;

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public Work saveWork(Work work) {
        return workRepository.save(work);
    }

    public List<Work> getAllWorks() {
        return workRepository.findAll();
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id).orElse(null);
    }
}
