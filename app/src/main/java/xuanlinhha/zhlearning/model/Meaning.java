package xuanlinhha.zhlearning.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by xuanlinhha on 20/5/18.
 */

@Entity
public class Meaning {
    @PrimaryKey
    @NonNull
    private String han;
    @ColumnInfo(name = "json")
    private String json;

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
}
