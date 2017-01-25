package ru.ztrap.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_IN;
import static android.util.TypedValue.COMPLEX_UNIT_MM;
import static android.util.TypedValue.COMPLEX_UNIT_PT;
import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static android.util.TypedValue.COMPLEX_UNIT_SP;
/**
 * <b color="#FFEE58">NOTICE:</b> Now supports only single-line<br>
 *
 * {@link ru.ztrap.views.R.styleable#FormattedEditText View Attrebutes}
 * */
public class FormattedEditText extends LinearLayout {
    private zWatcher watcher;
    
    private List<EditText> editTexts = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();
    private SparseBooleanArray full = new SparseBooleanArray();
    
    private StringBuilder builder = new StringBuilder();
    
    private int INPUT_TYPE = EditorInfo.TYPE_CLASS_NUMBER;
    private int textColor = Color.BLACK;
    private float textSize = 18;
    private @Units int textSizeUnit = COMPLEX_UNIT_SP;
    
    private String format = "+7 ({3}) {3}-{2}-{2}";
    
    @IntDef(flag = true, value = {COMPLEX_UNIT_PX, COMPLEX_UNIT_DIP, COMPLEX_UNIT_MM, COMPLEX_UNIT_SP, COMPLEX_UNIT_PT, COMPLEX_UNIT_IN})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private @interface Units{}
    
    public FormattedEditText(Context context) {
        this(context, null);
    }

    public FormattedEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public FormattedEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyAttrs(attrs);
    }

    @TargetApi(21)
    public FormattedEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyAttrs(attrs);
    }
    
    private void applyAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FormattedEditText);
        try {
            if (a.hasValue(R.styleable.FormattedEditText_z_format))
                format = a.getNonResourceString(R.styleable.FormattedEditText_z_format);
            textColor = a.getColor(R.styleable.FormattedEditText_z_textColor, textColor);
            textSize = a.getDimension(R.styleable.FormattedEditText_z_textSize, textSize);
        }finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        removeAllViews();
        textViews = new ArrayList<>();
        editTexts = new ArrayList<>();
        addViews();

        if (!editTexts.isEmpty()) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    editTexts.get(0).requestFocus();
                    ViewTreeObserver observer = getViewTreeObserver();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                        observer.removeGlobalOnLayoutListener(this);
                    } else {
                        observer.removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }
    
    /**
     * <p>
     * <b>Formatter tags:<br></b>
     * <ul>
     *     <b><u>Template: {3(N...)}</u></b><br>
     *     <br>
     *     <b>{}</b> - main dividers to insert {@link EditText}. Within these settings available tags:<br>
     *     <b>3</b> - count of maximum symbols. 0 = have no maximum<br>
     *     <b>()</b> - second dividers to set {@link EditText} some input types. Types:<br>
     * </ul>
     * <li type="disc"><b>NULL</b>     - {@link EditorInfo#TYPE_NULL}</li>
     * <br>
     * <li type="disc"><b>T</b>        - {@link EditorInfo#TYPE_CLASS_TEXT}</li>
     * <br>
     * <li type="circle"><b>TFCC</b>   - {@link EditorInfo#TYPE_TEXT_FLAG_CAP_CHARACTERS}</li>
     * <li type="circle"><b>TFCW</b>   - {@link EditorInfo#TYPE_TEXT_FLAG_CAP_WORDS}</li>
     * <li type="circle"><b>TFCS</b>   - {@link EditorInfo#TYPE_TEXT_FLAG_CAP_SENTENCES}</li>
     * <li type="circle"><b>TFACOR</b> - {@link EditorInfo#TYPE_TEXT_FLAG_AUTO_CORRECT}</li>
     * <li type="circle"><b>TFACOM</b> - {@link EditorInfo#TYPE_TEXT_FLAG_AUTO_COMPLETE}</li>
     * <li type="circle"><b>TFML</b>   - {@link EditorInfo#TYPE_TEXT_FLAG_MULTI_LINE}</li>
     * <li type="circle"><b>TFIML</b>  - {@link EditorInfo#TYPE_TEXT_FLAG_IME_MULTI_LINE}</li>
     * <li type="circle"><b>TFNS</b>   - {@link EditorInfo#TYPE_TEXT_FLAG_NO_SUGGESTIONS}</li>
     * <li type="circle"><b>TVN</b>    - {@link EditorInfo#TYPE_TEXT_VARIATION_NORMAL}</li>
     * <li type="circle"><b>TVU</b>    - {@link EditorInfo#TYPE_TEXT_VARIATION_URI}</li>
     * <li type="circle"><b>TVEA</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_EMAIL_ADDRESS}</li>
     * <li type="circle"><b>TVES</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_EMAIL_SUBJECT}</li>
     * <li type="circle"><b>TVSM</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_SHORT_MESSAGE}</li>
     * <li type="circle"><b>TVLM</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_LONG_MESSAGE}</li>
     * <li type="circle"><b>TVPN</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_PERSON_NAME}</li>
     * <li type="circle"><b>TVPA</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_POSTAL_ADDRESS}</li>
     * <li type="circle"><b>TVPASS</b> - {@link EditorInfo#TYPE_TEXT_VARIATION_PASSWORD}</li>
     * <li type="circle"><b>TVVP</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_VISIBLE_PASSWORD}</li>
     * <li type="circle"><b>TVWET</b>  - {@link EditorInfo#TYPE_TEXT_VARIATION_WEB_EDIT_TEXT}</li>
     * <li type="circle"><b>TVF</b>    - {@link EditorInfo#TYPE_TEXT_VARIATION_FILTER}</li>
     * <li type="circle"><b>TVPH</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_PHONETIC}</li>
     * <li type="circle"><b>TVWEA</b>  - {@link EditorInfo#TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS}</li>
     * <li type="circle"><b>TVWP</b>   - {@link EditorInfo#TYPE_TEXT_VARIATION_WEB_PASSWORD}</li>
     * <br>
     * <li type="disc"><b>N</b>        - {@link EditorInfo#TYPE_CLASS_NUMBER}</li>
     * <br>
     * <li type="circle"><b>NFS</b>    - {@link EditorInfo#TYPE_NUMBER_FLAG_SIGNED}</li>
     * <li type="circle"><b>NFD</b>    - {@link EditorInfo#TYPE_NUMBER_FLAG_DECIMAL}</li>
     * <li type="circle"><b>NVN</b>    - {@link EditorInfo#TYPE_NUMBER_VARIATION_NORMAL}</li>
     * <li type="circle"><b>NVP</b>    - {@link EditorInfo#TYPE_NUMBER_VARIATION_PASSWORD}</li>
     * <br>
     * <li type="disc"><b>P</b>        - {@link EditorInfo#TYPE_CLASS_PHONE}</li>
     * <br>
     * <li type="disc"><b>D</b>        - {@link EditorInfo#TYPE_CLASS_DATETIME}</li>
     * <br>
     * <li type="circle"><b>DVN</b>    - {@link EditorInfo#TYPE_DATETIME_VARIATION_NORMAL}</li>
     * <li type="circle"><b>DVD</b>    - {@link EditorInfo#TYPE_DATETIME_VARIATION_DATE}</li>
     * <li type="circle"><b>DVT</b>    - {@link EditorInfo#TYPE_DATETIME_VARIATION_TIME}</li>
     * */
    public void setFormat(String format){
        this.format = format;
        init();
    }
    
    /**
     * <p>Returns current format</p>
     * <b>See Also:</b><br>
     * {@link #setFormat(String) Available tags}<br>
     * */
    public String getFormat() {
        return format;
    }
    
    /**
     * Set the default text size to the given value, interpreted as "scaled
     * pixel" units.  This size is adjusted based on the current density and
     * user font size preference.
     *
     * @param size The scaled pixel size.
     */
    public void setTextSize(float size){
        setTextSize(size, COMPLEX_UNIT_SP);
    }
    
    /**
     * Set the default text size to a given unit and value.  See {@link
     * android.util.TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    public void setTextSize(float size, @Units int unit){
        textSize = size;
        textSizeUnit = unit;
        init();
    }
    
    /**
     * @return the size <b>(in pixels)</b> of the default text size in this TextView.
     */
    public float getTextSize() {
        return textSize;
    }
    
    /**
     * @return the size unit. See {@link
     * android.util.TypedValue} for the possible dimension units.
     */
    @Units
    public int getTextSizeUnit() {
        return textSizeUnit;
    }
    
    /**
     * Sets the text color for all the states (normal, selected,
     * focused) to be this color.
     *
     * @param resId A color resource ID.
     * Do not pass a color value. To set a color from a value, call
     * {@link #setTextColorInt(int)}.
     */
    public void setTextColorRes(@ColorRes int resId){
        setTextColorInt(ResourcesCompat.getColor(getResources(), resId, getResources().newTheme()));
    }
    
    /**
     * Sets the text color for all the states (normal, selected,
     * focused) to be this color.
     *
     * @param color A color value in the form 0xAARRGGBB.
     * Do not pass a resource ID. To get a color value from a resource ID, call
     * {@link #setTextColorRes(int)}.
     */
    public void setTextColorInt(@ColorInt int color){
        textColor = color;
        init();
    }
    
    /**
     * Gets the text color in the form 0xAARRGGBB.
     */
    public int getTextColor() {
        return textColor;
    }
    
    /**
     * Sets a zWatcher to the list of those whose methods are called
     * whenever text changed.
     */
    public void setWatcher(zWatcher watcher) {
        this.watcher = watcher;
    }
    
    /**
     * Gets current zWatcher for this FormattedEditText.
     */
    public zWatcher getWatcher() {
        return watcher;
    }
    
    /**
     * Gets the list of TextViews included in this FormattedEditText.
     */
    public List<TextView> getTextViews() {
        return textViews;
    }
    
    /**
     * Gets the list of EditTexts included in this FormattedEditText.
     */
    public List<EditText> getEditTexts() {
        return editTexts;
    }
    
    private void addViews() {
        builder = new StringBuilder(format);
        builder.trimToSize();

        while (!TextUtils.isEmpty(builder)){
            if (builder.toString().matches("\\{[0-9]+(\\([\\w, ]+\\))?\\}.*?")) {
                addEditText();
            } else {
                addTextView();
            }
        }
        
        if (!editTexts.isEmpty()) {
            editTexts.get(0).setTag(true);
            if (editTexts.size() > 1)
                editTexts.get(editTexts.size() - 1).setTag(false);
        }
    }

    private void addEditText() {
        int openBracketIndex = getOpenBracketIndex();
        final AppCompatEditText et = new AppCompatEditText(getContext());
        et.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat);
        et.setTextColor(textColor);
        et.setTextSize(textSizeUnit, textSize);
        int count = getLimitSymbols(openBracketIndex);
        if (count != 0) {
            String holder = "";
            for (int i = 1; i <= count; i++) holder += "0";
            et.setMinimumWidth((int) et.getPaint().measureText(holder) +
                    et.getCompoundPaddingLeft() + et.getCompoundPaddingRight());
            final int id = editTexts.size();
            et.addTextChangedListener(new zTextWatcher(holder) {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (watcher != null)
                        watcher.beforeTextChanged(id, s, start, count, after);
                }
    
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (watcher != null)
                        watcher.onTextChanged(id, s, start, before, count);
                }
    
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > maxLength) {
                        s.delete(maxLength, s.length());
                    } else{
                        full.put(id, false);
                        if (watcher != null)
                            watcher.afterTextChanged(id, s);
                    }
                    if (s.length() == maxLength) {
                        full.put(id, true);
                        checkAllFulled();
                        View next = et.focusSearch(FOCUS_FORWARD);
                        if (next != null) {
                            if (next.getTag() != null) {
                                if (!Boolean.valueOf(next.getTag().toString()))
                                    next.requestFocus();
                            } else next.requestFocus();
                        } else {
                            next = et.focusSearch(FOCUS_DOWN);
                            if (next != null)
                                next.requestFocus();
                        }
                    } else if (s.length() == 0) {
                        View previous = et.focusSearch(FOCUS_BACKWARD);
                        if (previous != null) {
                            if (previous.getTag() != null) {
                                if (Boolean.valueOf(previous.getTag().toString()))
                                    previous.requestFocus();
                            } else previous.requestFocus();
                        } else {
                            previous = et.focusSearch(FOCUS_UP);
                            if (previous != null)
                                previous.requestFocus();
                        }
                    }
                }
            });
        }
        et.setInputType(INPUT_TYPE);
        et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        editTexts.add(et);
        full.append(editTexts.size() - 1, false);
        addView(et);
    }
    
    private void checkAllFulled() {
        for (int i = 0; i < full.size(); i++) {
            if (!full.get(i))
                return;
        }
        String onlyEntered = "";
        for (EditText et : editTexts) {
            String entered = et.getText().toString();
            onlyEntered += TextUtils.isEmpty(entered) ? "" : entered;
        }
        String withFormat = "";
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView)child;
                String entered = tv.getText().toString();
                withFormat += TextUtils.isEmpty(entered) ? "" : entered;
            }
        }
        
        if (watcher != null)
            watcher.onAllCompleted(onlyEntered, withFormat);
    }
    
    private void addTextView() {
        AppCompatTextView tv = new AppCompatTextView(getContext());
        tv.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat);
        tv.setTextColor(textColor);
        tv.setTextSize(textSizeUnit, textSize);
        int openBracketIndex = getOpenBracketIndex();
        String replacement;
        if (openBracketIndex > 0) {
            replacement = builder.substring(0, openBracketIndex);
        }else {
            replacement = builder.toString();
        }
        tv.setText(replacement);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textViews.add(tv);
        addView(tv);
        builder.delete(0, replacement.length());
    }

    private int getOpenBracketIndex(){
        int index = -1;
        char[] chars = builder.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c != '{') continue;
            if (!checkShieldBefore(i)){
                index = i;
                break;
            }
        }
        return index;
    }
    
    private boolean checkShieldBefore(int index) {
        return builder.length() > 0 && index > 0 && builder.charAt(index - 1) == '\\';
    }

    private int getLimitSymbols(int startIndex){
        int limit = 0;
        if (startIndex < builder.length() && startIndex + 2 <= builder.length()){
            String limit_str = "";
            char[] chars = builder.toString().toCharArray();
            for (int i = startIndex; i < chars.length; i++) {
                char c = chars[i];
                String tempC = String.valueOf(c);
                if (tempC.equals("{")){
                    continue;
                }else if (tempC.matches("[0-9]")) {
                    limit_str += tempC;
                    continue;
                }else if (tempC.equals("(")){
                    setInputType(i);
                    chars = builder.toString().toCharArray();
                    i = startIndex;
                    limit_str = "";
                    continue;
                }else if (tempC.equals("}")){
                    builder.delete(startIndex, i + 1);
                }
                break;
            }
            if (!TextUtils.isEmpty(limit_str))
                limit = Integer.valueOf(limit_str);
        }
        return limit;
    }
    
    private void setInputType(int startIndex){
        INPUT_TYPE = EditorInfo.TYPE_NULL;
        int endIndex = -1;
        for (int i = startIndex; i < builder.length(); i++){
            char c = builder.charAt(i);
            if (c == ')'){
                endIndex = i;
                break;
            }
        }
        if (endIndex == -1) return;
        String[] inputs = builder.substring(startIndex + 1, endIndex).split(",");
        builder.delete(startIndex, endIndex + 1);
        for (String type : inputs) {
            switch (type){
                case "NULL":
                    INPUT_TYPE |= EditorInfo.TYPE_NULL;
                    break;
                case "T":
                    INPUT_TYPE |= EditorInfo.TYPE_CLASS_TEXT;
                    break;
                case "TFCC":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS;
                    break;
                case "TFCW":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS;
                    break;
                case "TFCS":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES;
                    break;
                case "TFACOR":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT;
                    break;
                case "TFACOM":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE;
                    break;
                case "TFML":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;
                    break;
                case "TFIML":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE;
                    break;
                case "TFNS":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                    break;
                case "TVN":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_NORMAL;
                    break;
                case "TVU":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_URI;
                    break;
                case "TVEA":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                    break;
                case "TVES":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
                    break;
                case "TVSM":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
                    break;
                case "TVLM":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE;
                    break;
                case "TVPN":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME;
                    break;
                case "TVPA":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
                    break;
                case "TVPASS":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                    break;
                case "TVVP":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                    break;
                case "TVWET":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
                    break;
                case "TVF":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_FILTER;
                    break;
                case "TVPH":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_PHONETIC;
                    break;
                case "TVWEA":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
                    break;
                case "TVWP":
                    INPUT_TYPE |= EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD;
                    break;
                case "N":
                    INPUT_TYPE |= EditorInfo.TYPE_CLASS_NUMBER;
                    break;
                case "NFS":
                    INPUT_TYPE |= EditorInfo.TYPE_NUMBER_FLAG_SIGNED;
                    break;
                case "NFD":
                    INPUT_TYPE |= EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;
                    break;
                case "NVN":
                    INPUT_TYPE |= EditorInfo.TYPE_NUMBER_VARIATION_NORMAL;
                    break;
                case "NVP":
                    INPUT_TYPE |= EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD;
                    break;
                case "P":
                    INPUT_TYPE |= EditorInfo.TYPE_CLASS_PHONE;
                    break;
                case "D":
                    INPUT_TYPE |= EditorInfo.TYPE_CLASS_DATETIME;
                    break;
                case "DVN":
                    INPUT_TYPE |= EditorInfo.TYPE_DATETIME_VARIATION_NORMAL;
                    break;
                case "DVD":
                    INPUT_TYPE |= EditorInfo.TYPE_DATETIME_VARIATION_DATE;
                    break;
                case "DVT":
                    INPUT_TYPE |= EditorInfo.TYPE_DATETIME_VARIATION_TIME;
                    break;
                default:
                    INPUT_TYPE = EditorInfo.TYPE_CLASS_NUMBER;
                    break;
            }
        }
    }

    private class zTextWatcher implements TextWatcher{
        int maxLength;
        zTextWatcher(String maxLengthFromText){
            maxLength = maxLengthFromText.length();
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
    
    public interface zWatcher{
        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * are about to be replaced by new text with length <code>after</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         * */
        void beforeTextChanged(int id, CharSequence s, int start, int count, int after);
        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * have just replaced old text that had length <code>before</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         */
        void onTextChanged(int id, CharSequence s, int start, int before, int count);
        /**
         * This method is called to notify you that, somewhere within
         * <code>s</code>, the text has been changed.
         * It is legitimate to make further changes to <code>s</code> from
         * this callback, but be careful not to get yourself into an infinite
         * loop, because any changes you make will cause this method to be
         * called again recursively.
         * (You are not told where the change took place because other
         * afterTextChanged() methods may already have made other changes
         * and invalidated the offsets.  But if you need to know here,
         * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
         * to mark your place and then look up from here where the span
         * ended up.
         */
        void afterTextChanged(int id, Editable s);
        /**
         * This method is called to notify you that, all EditTexts was fulled.
         * <code>onlyEntered</code> include collected data from all EditTexts,
         * <code>withFormat</code> include collected data from all EditTexts
         * and TextViews in right order. <code>withFormat</code> it is the
         * same that your completed template
         * */
        void onAllCompleted(String onlyEntered, String withFormat);
    }
    
    /**
     * Adapter for {@link zWatcher zWatcher} With it you can use all, some single,
     * or none method from zWatcher
     * */
    public static class zWatcherAdapter implements zWatcher{
        @Override
        public void beforeTextChanged(int id, CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(int id, CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(int id, Editable s) {}
        @Override
        public void onAllCompleted(String onlyEntered, String withFormat) {}
    }
}
