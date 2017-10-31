package pay4free.in.androideatitserver.model;

/**
 * Created by AAKASH on 17-10-2017.
 */

public class User {
    private String name,password,Phone,isStaff;

    public User()
    {

    }

    public User(String name, String password) {
        name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }
}
