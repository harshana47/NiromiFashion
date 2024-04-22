// Department.java (model class)
package lk.ijse.model;

public class Department {
    private String depId;
    private String name;
    private int staffCount;

    public Department(String depId, String name, int staffCount) {
        this.depId = depId;
        this.name =  name;
        this.staffCount = staffCount;
    }

    public Department(String id, String name, String depId, String position, String duty) {
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(int staffCount) {
        this.staffCount = staffCount;
    }

    @Override
    public String toString() {
        return "Department{" +
                "depId='" + depId + '\'' +
                ", name='" + name + '\'' +
                ", staffCount=" + staffCount +
                '}';
    }
}
