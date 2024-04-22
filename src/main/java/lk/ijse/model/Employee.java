package lk.ijse.model;

public class Employee {
    private String employeeId;
    private String name;
    private String depId;
    private String position;
    private String duty;

    public Employee(String employeeId, String name, String depId, String position, String duty) {
        this.employeeId = employeeId;
        this.name = name;
        this.depId = depId;
        this.position = position;
        this.duty = duty;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
}
