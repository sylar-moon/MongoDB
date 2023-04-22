package my.group.DTO;

import org.bson.Document;

import javax.validation.constraints.NotBlank;

public class Good {
    private String goodName;
    private Document typeId;

    public Good(String goodName, Document typeId) {
        this.goodName = goodName;
        this.typeId = typeId;

    }
    @NotBlank (message = "Yor name good is blank")
    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Document getTypeId() {
        return typeId;
    }

    public void setTypeId(Document typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Good{" +
                "good_name='" + goodName + '\'' +
                ", typeId=" + typeId +
                '}';
    }
}
