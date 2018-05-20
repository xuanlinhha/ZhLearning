package xuanlinhha.zhlearning.json;

import java.util.List;

/**
 * Created by xuanlinhha on 20/5/18.
 */

public class CCJson {
    private String han;
    private List<String> pinyin;
    private List<String> viet;
    private List<Phrase> phrases;

    public String getHan() {
        return han;
    }

    public void setHan(String han) {
        this.han = han;
    }

    public List<String> getPinyin() {
        return pinyin;
    }

    public void setPinyin(List<String> pinyin) {
        this.pinyin = pinyin;
    }

    public List<String> getViet() {
        return viet;
    }

    public void setViet(List<String> viet) {
        this.viet = viet;
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Phrase> phrases) {
        this.phrases = phrases;
    }
}
