package xuanlinhha.zhlearning;

import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
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
    private Boolean kbEnable;

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
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

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
        move(1);
    }

    private void prevHandler() {
        move(-1);
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<SavedItem> savedItems = db.savedItemDao().getSavedItems();
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
                    setText(editText, sb.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Loaded!", Toast.LENGTH_LONG).show();
            }
        }.execute();
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

    private void move(int step) {
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

        // update position
        int currentPos = editText.getSelectionStart();
        position = (currentPos + step) % editText.getText().length();
        if (position < 0) position += editText.getText().length();
        editText.setSelection(position);
        editText.getText().setSpan(new StyleSpan(android.graphics.Typeface.BOLD), position, position + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // load new character
        String han = String.valueOf(editText.getText().charAt(position));
        loadData(han);
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

    private void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    private boolean dbExist() {
        File dbFile = this.getDatabasePath(DB.DATABASE_NAME);
        return dbFile.exists();
    }

    public void onCheckboxClicked(View view) {
        this.kbEnable = ((CheckBox) view).isChecked();
        if (this.kbEnable) {
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.onTouchEvent(event);
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
                    }
                    return true;
                }
            });
        } else {
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.onTouchEvent(event);
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
            });
        }


    }

    public void onUp(View view) {
        move(-20);
    }

    public void onDown(View view) {
        move(20);
    }

    public void onEraseClicked(View view) {
        editText.setText("");
    }
}
