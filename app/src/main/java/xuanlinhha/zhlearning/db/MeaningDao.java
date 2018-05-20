package xuanlinhha.zhlearning.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import xuanlinhha.zhlearning.model.Meaning;

/**
 * Created by xuanlinhha on 20/5/18.
 */

@Dao
public interface MeaningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMeaning(Meaning meaning);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMeaning(List<Meaning> meanings);

    @Query("SELECT * FROM Meaning WHERE han == :han LIMIT 1")
    public LiveData<Meaning> getMeaning(String han);

    @Query("SELECT COUNT(*) FROM Meaning")
    public int count();
}
