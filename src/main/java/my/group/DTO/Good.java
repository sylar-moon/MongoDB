package my.group.DTO;

import org.bson.Document;

import javax.validation.constraints.NotBlank;

public class Good {
    private String goodName;
    private String type;

    public Good(String goodName, String type) {
        this.goodName = goodName;
        this.type = type;

    }
    @NotBlank (message = "Yor name good is blank")
    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

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
