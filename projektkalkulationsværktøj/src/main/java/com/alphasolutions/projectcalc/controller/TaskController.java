package com.alphasolutions.projectcalc.controller;

import com.alphasolutions.projectcalc.model.SubProject;
import com.alphasolutions.projectcalc.model.Task;
import com.alphasolutions.projectcalc.service.SubProjectService;
import com.alphasolutions.projectcalc.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final SubProjectService subProjectService;

    public TaskController(TaskService taskService, SubProjectService subProjectService) {
        this.taskService = taskService;
        this.subProjectService = subProjectService;
    }

    @GetMapping("/new")
    public String newForm(@RequestParam int subProjectId, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Task task = new Task();
        task.setSubProjectId(subProjectId);
        task.setStatus(TaskService.STATUS_NOT_STARTED);
        model.addAttribute("task", task);
        model.addAttribute("projectId", getProjectId(subProjectId));
        return "tasks/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Task task, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        try {
            taskService.createTask(task);
            return "redirect:/projects/" + getProjectId(task.getSubProjectId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("task", task);
            model.addAttribute("projectId", getProjectId(task.getSubProjectId()));
            return "tasks/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Optional<Task> taskOpt = taskService.getTaskById(id);
        if (taskOpt.isEmpty()) return "redirect:/projects";
        Task task = taskOpt.get();
        model.addAttribute("task", task);
        model.addAttribute("projectId", getProjectId(task.getSubProjectId()));
        return "tasks/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable int id, @ModelAttribute Task task,
                         HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        try {
            task.setId(id);
            taskService.updateTask(task);
            return "redirect:/projects/" + getProjectId(task.getSubProjectId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("task", task);
            model.addAttribute("projectId", getProjectId(task.getSubProjectId()));
            return "tasks/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, @RequestParam int subProjectId, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        taskService.deleteTask(id);
        return "redirect:/projects/" + getProjectId(subProjectId);
    }

    private int getProjectId(int subProjectId) {
        Optional<SubProject> sp = subProjectService.getSubProjectById(subProjectId);
        return sp.map(SubProject::getProjectId).orElse(0);
    }
}
