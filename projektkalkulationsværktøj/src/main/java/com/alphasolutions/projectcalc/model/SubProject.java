package com.alphasolutions.projectcalc.model;

import java.time.LocalDate;

public class SubProject {

    private int id;
    private String name;
    private String description;
    private LocalDate deadline;
    private int projectId;
    private double totalHours;
    private String status = "IN_PROGRESS";

    public SubProject() {}

    public SubProject(int id, String name, String description, LocalDate deadline, int projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.projectId = projectId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
