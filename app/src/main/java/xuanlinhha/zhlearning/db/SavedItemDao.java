package xuanlinhha.zhlearning.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import xuanlinhha.zhlearning.model.SavedItem;

/**
 * Created by xuanlinhha on 7/7/18.
 */
@Dao
public interface SavedItemDao {
    @Query("SELECT * FROM SavedItem")
    public LiveData<List<SavedItem>> getSavedItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSavedItem(SavedItem savedItem);

    @Delete
    public void delete(SavedItem savedItem);

    @Query("DELETE FROM SavedItem")
    void deleteAll();
}
