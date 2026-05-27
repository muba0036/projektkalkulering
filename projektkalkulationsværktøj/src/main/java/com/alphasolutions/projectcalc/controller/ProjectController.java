package com.alphasolutions.projectcalc.controller;

import com.alphasolutions.projectcalc.model.Project;
import com.alphasolutions.projectcalc.model.SubProject;
import com.alphasolutions.projectcalc.model.Task;
import com.alphasolutions.projectcalc.model.User;
import com.alphasolutions.projectcalc.service.ProjectService;
import com.alphasolutions.projectcalc.service.SubProjectService;
import com.alphasolutions.projectcalc.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final SubProjectService subProjectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, SubProjectService subProjectService, TaskService taskService) {
        this.projectService = projectService;
        this.subProjectService = subProjectService;
        this.taskService = taskService;
    }

    @GetMapping
    public String listProjects(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Project> projects = projectService.getProjectsByUserId(user.getId());
        model.addAttribute("projects", projects);
        model.addAttribute("user", user);
        return "projects/list";
    }

    @GetMapping("/new")
    public String newProjectForm(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Project project = new Project();
        project.setStatus("NOT_STARTED");
        model.addAttribute("project", project);
        return "projects/form";
    }

    @PostMapping("/new")
    public String createProject(@ModelAttribute Project project, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        try {
            project.setUserId(user.getId());
            projectService.createProject(project);
            return "redirect:/projects";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("project", project);
            return "projects/form";
        }
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Optional<Project> projectOpt = projectService.getProjectById(id);
        if (projectOpt.isEmpty()) return "redirect:/projects";

        Project project = projectOpt.get();
        List<SubProject> subProjects = subProjectService.getSubProjectsByProjectId(id);

        // Build ordered map: subproject → tasks
        Map<SubProject, List<Task>> tasksBySubProject = new LinkedHashMap<>();
        for (SubProject sp : subProjects) {
            tasksBySubProject.put(sp, taskService.getTasksBySubProjectId(sp.getId()));
        }

        // Pre-compute per-subproject totals for the template
        Map<Integer, Map<String, Double>> spTotals = new LinkedHashMap<>();
        for (Map.Entry<SubProject, List<Task>> entry : tasksBySubProject.entrySet()) {
            List<Task> tasks = entry.getValue();
            Map<String, Double> t = new LinkedHashMap<>();
            double hours = tasks.stream().mapToDouble(Task::getEstimatedHours).sum();
            double laborTotal = tasks.stream().mapToDouble(Task::getLaborTotal).sum();
            double units = tasks.stream().mapToDouble(Task::getMaterialUnits).sum();
            double materialsTotal = tasks.stream().mapToDouble(Task::getMaterialsTotal).sum();
            double budgeted = tasks.stream().mapToDouble(Task::getBudgetedCost).sum();
            double actual = tasks.stream().mapToDouble(Task::getActualCost).sum();
            t.put("hours", hours);
            t.put("rate", hours > 0 ? laborTotal / hours : 0);
            t.put("laborTotal", laborTotal);
            t.put("units", units);
            t.put("costPerUnit", units > 0 ? materialsTotal / units : 0);
            t.put("materialsTotal", materialsTotal);
            t.put("budgeted", budgeted);
            t.put("actual", actual);
            t.put("diff", budgeted - actual);
            spTotals.put(entry.getKey().getId(), t);
        }

        // Overall summary totals
        List<Task> allTasks = tasksBySubProject.values().stream().flatMap(Collection::stream).toList();
        double totalHours = allTasks.stream().mapToDouble(Task::getEstimatedHours).sum();
        double totalLaborCost = allTasks.stream().mapToDouble(Task::getLaborTotal).sum();
        double totalUnits = allTasks.stream().mapToDouble(Task::getMaterialUnits).sum();
        double totalMaterialsCost = allTasks.stream().mapToDouble(Task::getMaterialsTotal).sum();
        double totalBudgeted = allTasks.stream().mapToDouble(Task::getBudgetedCost).sum();
        double totalActual = totalLaborCost + totalMaterialsCost;

        model.addAttribute("project", project);
        model.addAttribute("tasksBySubProject", tasksBySubProject);
        model.addAttribute("spTotals", spTotals);
        model.addAttribute("summaryHours", totalHours);
        model.addAttribute("summaryAvgRate", totalHours > 0 ? totalLaborCost / totalHours : 0);
        model.addAttribute("summaryLaborTotal", totalLaborCost);
        model.addAttribute("summaryUnits", totalUnits);
        model.addAttribute("summaryAvgCostPerUnit", totalUnits > 0 ? totalMaterialsCost / totalUnits : 0);
        model.addAttribute("summaryMaterialsTotal", totalMaterialsCost);
        model.addAttribute("summaryBudgeted", totalBudgeted);
        model.addAttribute("summaryActual", totalActual);
        model.addAttribute("summaryDiff", totalBudgeted - totalActual);
        return "projects/detail";
    }

    @GetMapping("/{id}/edit")
    public String editProjectForm(@PathVariable int id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        Optional<Project> projectOpt = projectService.getProjectById(id);
        if (projectOpt.isEmpty()) return "redirect:/projects";
        model.addAttribute("project", projectOpt.get());
        return "projects/form";
    }

    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable int id, @ModelAttribute Project project,
                                HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        try {
            project.setId(id);
            project.setUserId(user.getId());
            projectService.updateProject(project);
            return "redirect:/projects/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("project", project);
            return "projects/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable int id, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}
