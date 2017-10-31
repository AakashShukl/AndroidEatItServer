package pay4free.in.androideatitserver.model;

/**
 * Created by AAKASH on 19-10-2017.
 */

public class Category {
    private String Name;
    private String Image;

    public Category()
    {

    }

    public Category(String name, String image) {
        Name = name;
        Image = image;

    }

    public String getName() {
        return Name;
    }

    public String getImage() {
        return Image;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setImage(String image) {
        Image = image;
    }
}
