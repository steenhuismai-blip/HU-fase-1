public class Employee {
    String employeeId;
    String password;

    public Employee(String id, String pwd) {
        this.employeeId = id;
        this.password = pwd;
    }

    public boolean login(String inputPassword) {
        return password.equals(inputPassword);
    }
}
