package xuanlinhha.zhlearning.db;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import xuanlinhha.zhlearning.model.CC;
import xuanlinhha.zhlearning.model.Meaning;

/**
 * Created by xuanlinhha on 20/5/18.
 */

public class DBInitializer extends AsyncTask<Void, Void, Void> {
    private static final int BATCH_SIZE = 500;
    private Context context;
    private DB db;
    ProgressDialog dialog;

    public DBInitializer(Context context, DB db) {
        this.context = context;
        this.db = db;
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        long start = System.currentTimeMillis();
        insertCCs();
        updateSVGs();
        insertMeanings("char_meanings.txt.zip");
        insertMeanings("word_meanings.txt.zip");
//        long end = System.currentTimeMillis();
//        System.out.println("Importing time = " + (end - start));
//        System.out.println("CC count() = " + db.ccDao().count());
//        System.out.println("MeaningJson count() = " + db.meaningDao().count());
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Initializing data, please wait ...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        if (dialog.isShowing()) {
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }

    private void insertCCs() {
        AssetManager am = context.getAssets();
        ZipInputStream zis = null;
        // dictionary
        try {
            List<CC> ccs = new ArrayList<>();
            zis = new ZipInputStream(am.open("dictionary.txt.zip"));
            zis.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String han = StringUtils.substringBetween(line, "\"han\":\"", "\",\"viet\"");
                CC cc = new CC();
                cc.setHan(han);
                cc.setJson(line);
                ccs.add(cc);
                if (ccs.size() == BATCH_SIZE) {
                    db.ccDao().insertCCs(ccs);
                    ccs.clear();
                }
            }
            db.ccDao().insertCCs(ccs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateSVGs() {
        AssetManager am = context.getAssets();
        ZipInputStream zis = null;
        Gson gson = new Gson();
        // dictionary
        try {
            List<CC> ccs = new ArrayList<>();
            zis = new ZipInputStream(am.open("svg.txt.zip"));
            zis.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
            String line = "";
            while ((line = reader.readLine()) != null) {
                CC cc = gson.fromJson(line, CC.class);
                db.ccDao().update(cc.getHan(), cc.getSvg());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void insertMeanings(String file) {
        AssetManager am = context.getAssets();
        ZipInputStream zis = null;
        Gson gson = new Gson();
        // dictionary
        try {
            List<Meaning> ms = new ArrayList<>();
            zis = new ZipInputStream(am.open(file));
            zis.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String han = StringUtils.substringBetween(line, "\"han\":\"", "\",\"viet\"");

                Meaning m = new Meaning();
                m.setHan(han);
                m.setJson(line);
                ms.add(m);
                if (ms.size() == BATCH_SIZE) {
                    db.meaningDao().insertMeaning(ms);
                    ms.clear();
                }
            }
            db.meaningDao().insertMeaning(ms);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
