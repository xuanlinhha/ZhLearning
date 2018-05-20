package xuanlinhha.zhlearning.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import xuanlinhha.zhlearning.model.CC;

/**
 * Created by xuanlinhha on 20/5/18.
 */

@Dao
public interface CCDao {
    @Query("SELECT * FROM CC WHERE han == :han LIMIT 1")
    public LiveData<CC> getCC(String han);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCCs(List<CC> ccs);

    @Query("UPDATE cc SET svg= :svg WHERE han = :han")
    public void update(String han, String svg);

    @Query("SELECT COUNT(*) FROM CC")
    public int count();
}
