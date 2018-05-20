package xuanlinhha.zhlearning.format;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import xuanlinhha.zhlearning.db.DB;
import xuanlinhha.zhlearning.json.CCJson;
import xuanlinhha.zhlearning.json.Phrase;
import xuanlinhha.zhlearning.model.CC;

/**
 * Created by xuanlinhha on 20/5/18.
 */

public class Formatter {
    private List<TextView> tvs;
    private Context context;
    private DB db;
    private CCJson ccJson;

    public Formatter(CC cc, Context context, DB db) {
        Gson g = new Gson();
        this.ccJson = g.fromJson(cc.getJson(), CCJson.class);
        this.context = context;
        this.db = db;
        tvs = new ArrayList<>();
    }

    public void format() {
        formatHan();
        formatPinyin();
        formatViet();
        formatPhrase();
    }

    private void formatHan() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int tmp = 0;
        ssb.append("Han: ");
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), tmp, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tmp = ssb.length();
        ssb.append(ccJson.getHan());
        ssb.setSpan(new MyClickableSpan(ccJson.getHan(), context, db), tmp, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tv = new TextView(context);
        tv.setText(ssb);
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tvs.add(tv);
    }

    private void formatPinyin() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("Pinyin: ");
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(TextUtils.join(",", ccJson.getPinyin()));
        TextView tv = new TextView(context);
        tv.setText(ssb);
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tvs.add(tv);
    }

    private void formatViet() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("Viet: ");
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(TextUtils.join(",", ccJson.getViet()));
        TextView tv = new TextView(context);
        tv.setText(ssb);
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tvs.add(tv);
    }

    private void formatPhrase() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int tmp = 0;
        ssb.append("Phrases: ");
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), tmp, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        for (int i = 0; i < ccJson.getPhrases().size(); i++) {
            Phrase ph = ccJson.getPhrases().get(i);
            ssb.append("[");
            tmp = ssb.length();
            ssb.append(ph.getHan());
            ssb.setSpan(new MyClickableSpan(ph.getHan(), context, db), tmp, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append("]");
            ssb.append(ph.getViet());
            if (i != ccJson.getPhrases().size() - 1) {
                ssb.append(", ");
            }
        }
        TextView tv = new TextView(context);
        tv.setText(ssb);
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tvs.add(tv);
    }

    public List<TextView> getTvs() {
        return tvs;
    }

    public void setTvs(List<TextView> tvs) {
        this.tvs = tvs;
    }
}
