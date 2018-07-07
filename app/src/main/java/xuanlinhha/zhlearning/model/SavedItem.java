package xuanlinhha.zhlearning.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by xuanlinhha on 7/7/18.
 */

@Entity
public class SavedItem {
    @PrimaryKey
    @NonNull
    private String han;

    public SavedItem(String han) {
        this.han = han;
    }

    @NonNull
    public String getHan() {
        return han;
    }

    public void setHan(@NonNull String han) {
        this.han = han;
    }
}
