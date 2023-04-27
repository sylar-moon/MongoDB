package my.group.good;

import my.group.validator.CheckForSwears;
import my.group.validator.CheckTerrorism;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class Good {
    private String goodName;
    private String type;

    public Good(String goodName, String type) {
        this.goodName = goodName;
        this.type = type;

    }
    @NotBlank (message = "Yor name good is blank")
    @Length (message = "Good name longer than 15 characters",max = 15)
    @Length (message = "Good name length is less than 2 characters",min = 2)
    @CheckTerrorism(message = "This product name is associated with terrorism")
    @CheckForSwears(message = "This product name contains swear words")
    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }


    @CheckTerrorism(message = "This product type is associated with terrorism")
    @CheckForSwears(message = "This product type contains swear words")
    @Length (message = "Good type longer than 15 characters",max = 15)
    @Length (message = "Good type length is less than 2 characters",min = 2)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Good{" +
                "good_name='" + goodName + '\'' +
                ", typeId=" + type +
                '}';
    }
}
