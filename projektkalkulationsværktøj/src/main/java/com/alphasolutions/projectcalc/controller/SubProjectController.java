package com.alphasolutions.projectcalc.controller;

import com.alphasolutions.projectcalc.model.SubProject;
import com.alphasolutions.projectcalc.model.Task;
import com.alphasolutions.projectcalc.service.SubProjectService;
import com.alphasolutions.projectcalc.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subprojects")
public class SubProjectController {

    private final SubProjectService subProjectService;
    private final TaskService taskService;

    public SubProjectController(SubProjectService subProjectService, TaskService taskService) {
        this.subProjectService = subProjectService;
        this.taskService = taskService;
    }

    @GetMapping("/new")
    public String newForm(@RequestParam int projectId, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        SubProject sp = new SubProject();
        sp.setProjectId(projectId);
        model.addAttribute("subProject", sp);
        return "subprojects/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute SubProject subProject,
                         HttpSession session,
                         Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        try {
            subProjectService.createSubProject(subProject);
            return "redirect:/projects/" + subProject.getProjectId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("subProject", subProject);
            return "subprojects/form";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Optional<SubProject> spOpt = subProjectService.getSubProjectById(id);
        if (spOpt.isEmpty()) return "redirect:/projects";
        List<Task> tasks = taskService.getTasksBySubProjectId(id);
        model.addAttribute("subProject", spOpt.get());
        model.addAttribute("tasks", tasks);
        model.addAttribute("co2Footprint", taskService.calculateCo2Footprint(tasks));
        return "subprojects/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Optional<SubProject> spOpt = subProjectService.getSubProjectById(id);
        if (spOpt.isEmpty()) return "redirect:/projects";
        model.addAttribute("subProject", spOpt.get());
        return "subprojects/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable int id,
                         @ModelAttribute SubProject subProject,
                         HttpSession session,
                         Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        try {
            subProject.setId(id);
            subProjectService.updateSubProject(subProject);
            return "redirect:/subprojects/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("subProject", subProject);
            return "subprojects/form";
        }
    }

    @PostMapping("/{id}/done")
    public String markDone(@PathVariable int id,
                           @RequestParam int projectId,
                           HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        subProjectService.markDone(id);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id,
                         @RequestParam int projectId,
                         HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        subProjectService.deleteSubProject(id);
        return "redirect:/projects/" + projectId;
    }
}
