package com.alphasolutions.projectcalc.model;

import java.time.LocalDate;

public class Task {

    private int id;
    private String name;
    private String description;
    private double estimatedHours;
    private double actualHours;
    private double hourlyRate;
    private double materialUnits;
    private double materialCostPerUnit;
    private double budgetedCost;
    private LocalDate deadline;
    private String status;
    private int subProjectId;

    public Task() {}

    public Task(int id, String name, String description, double estimatedHours,
                double actualHours, LocalDate deadline, String status, int subProjectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.deadline = deadline;
        this.status = status;
        this.subProjectId = subProjectId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }

    public double getActualHours() { return actualHours; }
    public void setActualHours(double actualHours) { this.actualHours = actualHours; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getMaterialUnits() { return materialUnits; }
    public void setMaterialUnits(double materialUnits) { this.materialUnits = materialUnits; }

    public double getMaterialCostPerUnit() { return materialCostPerUnit; }
    public void setMaterialCostPerUnit(double materialCostPerUnit) { this.materialCostPerUnit = materialCostPerUnit; }

    public double getBudgetedCost() { return budgetedCost; }
    public void setBudgetedCost(double budgetedCost) { this.budgetedCost = budgetedCost; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSubProjectId() { return subProjectId; }
    public void setSubProjectId(int subProjectId) { this.subProjectId = subProjectId; }

    public double getLaborTotal() { return estimatedHours * hourlyRate; }
    public double getMaterialsTotal() { return materialUnits * materialCostPerUnit; }
    public double getActualCost() { return getLaborTotal() + getMaterialsTotal(); }
    public double getDifference() { return budgetedCost - getActualCost(); }
}
