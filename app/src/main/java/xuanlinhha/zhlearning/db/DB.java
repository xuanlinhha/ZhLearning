package xuanlinhha.zhlearning.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import xuanlinhha.zhlearning.model.CC;
import xuanlinhha.zhlearning.model.Meaning;
import xuanlinhha.zhlearning.model.SavedItem;

/**
 * Created by xuanlinhha on 20/5/18.
 */

@Database(entities = {CC.class, Meaning.class, SavedItem.class}, version = 1)
public abstract class DB extends RoomDatabase {
    public static String DATABASE_NAME = "dictionary";

    public abstract CCDao ccDao();

    public abstract MeaningDao meaningDao();

    public abstract SavedItemDao savedItemDao();
}
