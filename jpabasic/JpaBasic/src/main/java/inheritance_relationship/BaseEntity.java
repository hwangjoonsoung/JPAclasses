package inheritance_relationship;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

    private String BaseCol1;
    private String BaseCol2;
    private String BaseCol3;
    private String BaseCol4;


    public String getBaseCol1() {
        return BaseCol1;
    }

    public void setBaseCol1(String baseCol1) {
        BaseCol1 = baseCol1;
    }

    public String getBaseCol2() {
        return BaseCol2;
    }

    public void setBaseCol2(String baseCol2) {
        BaseCol2 = baseCol2;
    }

    public String getBaseCol3() {
        return BaseCol3;
    }

    public void setBaseCol3(String baseCol3) {
        BaseCol3 = baseCol3;
    }

    public String getBaseCol4() {
        return BaseCol4;
    }

    public void setBaseCol4(String baseCol4) {
        BaseCol4 = baseCol4;
    }
}
