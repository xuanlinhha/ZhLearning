package xuanlinhha.zhlearning.format;

import android.app.Dialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebView;

import com.google.gson.Gson;

import xuanlinhha.zhlearning.R;
import xuanlinhha.zhlearning.db.DB;
import xuanlinhha.zhlearning.json.MeaningJson;
import xuanlinhha.zhlearning.model.Meaning;

/**
 * Created by xuanlinhha on 20/5/18.
 */

public class MyClickableSpan extends ClickableSpan {
    private String han;
    private Context context;
    private DB db;


    public MyClickableSpan(String han, Context context, DB db) {
        this.han = han;
        this.context = context;
        this.db = db;
    }

    @Override
    public void onClick(View view) {
        db.meaningDao().getMeaning(han).observe((LifecycleOwner) context, new Observer<Meaning>() {
            @Override
            public void onChanged(@Nullable Meaning m) {
                if (m != null) {
                    Gson g = new Gson();
                    MeaningJson mJson = g.fromJson(m.getJson(), MeaningJson.class);
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.meaning_dialog);
                    dialog.setTitle(mJson.getHan() + ": " + TextUtils.join(",", mJson.getViet()));
                    WebView meaningWebview = (WebView) dialog.findViewById(R.id.meaningWebview);
                    StringBuilder sb = new StringBuilder();
                    sb.append("<ol>");
                    for (String meaning : mJson.getMeanings()) {
                        sb.append("<li>");
                        sb.append(meaning);
                        sb.append("</li>");
                    }
                    sb.append("</ol>");
                    meaningWebview.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
                    dialog.show();
                }
            }
        });
    }
}