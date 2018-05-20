package xuanlinhha.zhlearning.json;

import java.util.List;

/**
 * Created by xuanlinhha on 20/5/18.
 */

public class MeaningJson {
    private String han;
    private List<String> viet;
    private List<String> meanings;

    public String getHan() {
        return han;
    }

    public void setHan(String han) {
        this.han = han;
    }

    public List<String> getViet() {
        return viet;
    }

    public void setViet(List<String> viet) {
        this.viet = viet;
    }

    public List<String> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<String> meanings) {
        this.meanings = meanings;
    }
}
