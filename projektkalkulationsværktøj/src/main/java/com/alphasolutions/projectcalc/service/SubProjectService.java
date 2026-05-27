package com.alphasolutions.projectcalc.service;

import com.alphasolutions.projectcalc.model.SubProject;
import com.alphasolutions.projectcalc.repository.SubProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;

    public SubProjectService(SubProjectRepository subProjectRepository) {
        this.subProjectRepository = subProjectRepository;
    }

    public List<SubProject> getSubProjectsByProjectId(int projectId) {
        List<SubProject> list = subProjectRepository.findByProjectId(projectId);
        for (SubProject sp : list) {
            sp.setTotalHours(subProjectRepository.sumEstimatedHours(sp.getId()));
        }
        return list;
    }

    public Optional<SubProject> getSubProjectById(int id) {
        Optional<SubProject> spOpt = subProjectRepository.findById(id);
        spOpt.ifPresent(sp -> sp.setTotalHours(subProjectRepository.sumEstimatedHours(sp.getId())));
        return spOpt;
    }

    public int createSubProject(SubProject subProject) {
        validate(subProject);
        return subProjectRepository.save(subProject);
    }

    public int updateSubProject(SubProject subProject) {
        validate(subProject);
        return subProjectRepository.update(subProject);
    }

    public int deleteSubProject(int id) {
        return subProjectRepository.deleteById(id);
    }

    public int markDone(int id) {
        return subProjectRepository.updateStatus(id, "DONE");
    }

    private void validate(SubProject subProject) {
        if (subProject.getName() == null || subProject.getName().isBlank()) {
            throw new IllegalArgumentException("Delprojekt navn er paakraevet");
        }
        if (subProject.getProjectId() <= 0) {
            throw new IllegalArgumentException("Delprojekt skal tilhoere et projekt");
        }
    }
}
