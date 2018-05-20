package xuanlinhha.zhlearning.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by xuanlinhha on 20/5/18.
 */

@Entity
public class CC {
    @PrimaryKey
    @NonNull
    private String han;
    @ColumnInfo(name = "json")
    private String json;
    @ColumnInfo(name = "svg")
    private String svg;

    public String getHan() {
        return han;
    }

    public void setHan(String han) {
        this.han = han;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
}
