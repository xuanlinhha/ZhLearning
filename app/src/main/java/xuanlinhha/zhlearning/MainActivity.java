package xuanlinhha.zhlearning;

import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xuanlinhha.zhlearning.db.DB;
import xuanlinhha.zhlearning.db.DBInitializer;
import xuanlinhha.zhlearning.format.Formatter;
import xuanlinhha.zhlearning.model.CC;
import xuanlinhha.zhlearning.model.SavedItem;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    private DB db;
    private EditText editText;
    private ImageButton goButton;
    private ImageButton replayButton;
    private WebView webView;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private ImageButton listButton;
    private ImageButton resetButton;
    private LinearLayout textContainer;
    private int position = 0;
    private CC curCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init db if empty
        db = Room.databaseBuilder(getApplicationContext(),
                DB.class, DB.DATABASE_NAME).build();
        if (!dbExist()) {
            DBInitializer dbInitializer = new DBInitializer(context, db);
            dbInitializer.execute();
        }

        // views
        editText = (EditText) findViewById(R.id.editText);
        goButton = findViewById(R.id.goButton);
        replayButton = findViewById(R.id.replayButton);
        webView = findViewById(R.id.webView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        listButton = findViewById(R.id.listButton);
        resetButton = findViewById(R.id.resetButton);
        textContainer = findViewById(R.id.container);

        // listeners
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHandler();
            }
        });
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayHandler();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevHandler();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextHandler();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHandler();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHandler();
            }
        });
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listHandler();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetHandler();
            }
        });

    }

    private void goHandler() {
        if (TextUtils.isEmpty(editText.getText())) {
            return;
        }
        // reset old position
        StyleSpan[] ss = editText.getText().getSpans(position, position + 1, StyleSpan.class);
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
                editText.getText().removeSpan(ss[i]);
            }
        }

        // bold new position
        position = editText.getSelectionStart();
        if (position == editText.getText().length()) {
            position = 0;
            editText.setSelection(0);
        }
        editText.getText().setSpan(new StyleSpan(android.graphics.Typeface.BOLD), position, position + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        // load data based on code point
        String han = String.valueOf(editText.getText().charAt(position));
        loadData(han);
    }

    private void replayHandler() {
        if (curCC != null) {
            webView.loadDataWithBaseURL(null, curCC.getSvg(), "text/html", "UTF-8", null);
            webView.refreshDrawableState();
        }
    }

    private void nextHandler() {
        if (TextUtils.isEmpty(editText.getText())) {
            return;
        }
        // reset old position
        StyleSpan[] ss = editText.getText().getSpans(position, position + 1, StyleSpan.class);
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
                editText.getText().removeSpan(ss[i]);
            }
        }

        // increase cursor by 1 and bold
        int currentPos = editText.getSelectionStart();
        position = (currentPos + 1) % editText.getText().length();
        editText.setSelection(position);
        editText.getText().setSpan(new StyleSpan(android.graphics.Typeface.BOLD), position, position + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // load data based on code point
        String han = String.valueOf(editText.getText().charAt(position));
        loadData(han);
    }

    private void prevHandler() {
        if (TextUtils.isEmpty(editText.getText())) {
            return;
        }
        // reset old position
        StyleSpan[] ss = editText.getText().getSpans(position, position + 1, StyleSpan.class);
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
                editText.getText().removeSpan(ss[i]);
            }
        }

        // decrease cursor by 1 and bold
        int currentPos = editText.getSelectionStart();
        position = (currentPos - 1) % editText.getText().length();
        if (position < 0) position += editText.getText().length();
        editText.setSelection(position);
        editText.getText().setSpan(new StyleSpan(android.graphics.Typeface.BOLD), position, position + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // load data based on code point
        String han = String.valueOf(editText.getText().charAt(position));
        loadData(han);
    }

    private void addHandler() {
        position = editText.getSelectionStart();
        final String han = (position < editText.getText().length()) ? String.valueOf(editText.getText().charAt(position)) : "";
        if (!TextUtils.isEmpty(han)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.savedItemDao().insertSavedItem(new SavedItem(han));
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                }
            }.execute();
        }
    }

    private void deleteHandler() {
        position = editText.getSelectionStart();
        final String han = (position < editText.getText().length()) ? String.valueOf(editText.getText().charAt(position)) : "";
        if (!TextUtils.isEmpty(han)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.savedItemDao().delete(new SavedItem(han));
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_LONG).show();
                }
            }.execute();
        }
    }

    private void listHandler() {
        db.savedItemDao().getSavedItems().observe(this, new Observer<List<SavedItem>>() {
            @Override
            public void onChanged(@Nullable List<SavedItem> savedItems) {
                if (!savedItems.isEmpty()) {
                    Collections.sort(savedItems, new Comparator<SavedItem>() {
                        @Override
                        public int compare(SavedItem i1, SavedItem i2) {
                            return i1.getHan().compareTo(i2.getHan());
                        }
                    });
                    StringBuilder sb = new StringBuilder();
                    for (SavedItem item : savedItems) {
                        sb.append(item.getHan());
                    }
                    editText.setText(sb.toString());
                }
            }
        });
    }

    private void resetHandler() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.savedItemDao().deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Reset!", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void loadData(String han) {
        db.ccDao().getCC(han).observe(this, new Observer<CC>() {
            @Override
            public void onChanged(@Nullable CC cc) {
                if (cc != null) {
                    curCC = cc;
                    // svg
                    webView.loadDataWithBaseURL(null, curCC.getSvg(), "text/html", "UTF-8", null);
                    webView.refreshDrawableState();
                    // view
                    textContainer.removeAllViews();
                    Formatter formatter = new Formatter(curCC, context, db);
                    formatter.format();
                    for (TextView tv : formatter.getTvs()) {
                        textContainer.addView(tv);
                    }
                }
            }
        });
    }

    private boolean dbExist() {
        File dbFile = this.getDatabasePath(DB.DATABASE_NAME);
        return dbFile.exists();
    }
}
