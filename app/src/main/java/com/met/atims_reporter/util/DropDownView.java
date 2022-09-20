package com.met.atims_reporter.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.met.atims_reporter.R;

import java.util.ArrayList;
import java.util.List;


public class DropDownView extends androidx.appcompat.widget.AppCompatEditText implements View.OnClickListener, AdapterView.OnItemClickListener, PopupWindow.OnDismissListener, AdapterView.OnItemSelectedListener {

    public interface OnItemSelectedListener {
        void onSelected(DropDownItem item);
    }

    private void onSelected(DropDownItem item) {
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onSelected(item);
        }
    }

    private PopupWindow pw;
    private ListView lv;
    private PopupListItemAdapter adapter;
    private List<DropDownItem> list;
    private int selectedPosition;
    private OnClickListener onClickListener = null;
    private AdapterView.OnItemClickListener onItemClickListener;
    private OnItemSelectedListener onItemSelectedListener;
    private int popupHeight = 280, popupWidth = 0;
    private EditText etSearch;
    private LinearLayout ll;

    // private final float[] r = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
    private final float[] r = new float[]{1, 1, 1, 1, 1, 1, 1, 1};
    private Drawable listDrawable;
    private Context mContext;

    private String bg_color = "#ffffff", divider_color = "#ffffff", tv_color = "#000000";

    public void setDropdownBGColor(String bg_color) {
        this.bg_color = bg_color;
    }

    public void setDropdownBG(Drawable listDrawable) {
        this.listDrawable = listDrawable;
    }

    public void setDividerColor(String divider_color) {
        this.divider_color = divider_color;
    }

    public void setTextColor(String tv_color) {
        this.tv_color = tv_color;

    }

    public void setPopupWith(int popupWidth) {
        this.popupWidth = popupWidth;
    }

    public void setPopupHeight(int popupHeight) {
        this.popupHeight = popupHeight;
    }

    public DropDownView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DropDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initFromXML(context, attrs);
        init();
    }

    public DropDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            if (listDrawable == null)
                listDrawable = getRoundDrawable();
            super.setOnClickListener(this);
        }
    }

    private void initFromXML(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownViewForXML);

            if (a.length() > 0) {
                for (int i = 0; i < a.length(); i++) {
                    int attr = a.getIndex(i);
                    if (attr == R.styleable.DropDownViewForXML_popup_height) {
                        int height = ConversionUtil.INSTANCE.dpToPx(popupHeight, mContext);
                        height = a.getInteger(attr, height);
                        popupHeight = ConversionUtil.INSTANCE.dpToPx(height, mContext);
                        Log.d("popupHeight", "PopUpHeight: " + popupHeight);
                    }
                    if (attr == R.styleable.DropDownViewForXML_popup_width) {
                        int width = a.getInteger(attr, 0);
                        popupWidth = ConversionUtil.INSTANCE.dpToPx(width, mContext);
                        Log.d("popupHeight", "popupWidth: " + popupWidth);
                    }

                    if (attr == R.styleable.DropDownViewForXML_popup_bg) {
                        Drawable drawable = a
                                .getDrawable(R.styleable.DropDownViewForXML_popup_bg);
                        if (drawable != null) {
                            listDrawable = drawable;
                        }
                    }

                }
            }
            a.recycle();
        }

        if (popupWidth == 0) {
            ViewTreeObserver vto = this.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    DropDownView.this
                            .getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                    popupWidth = DropDownView.this.getWidth();
                }
            });

        }

        setDropdownBG(ContextCompat.getDrawable(context, R.drawable.rounded_white));
    }

    private Drawable getRoundDrawable() {
        if (listDrawable == null) {
            ShapeDrawable drawable = new ShapeDrawable();

            if (!"".equals(bg_color)) {
                drawable.getPaint().setColor(Color.parseColor(bg_color));
            } else {
                drawable.getPaint().setColor(Color.parseColor("#4ca3cf"));
            }

            drawable.setShape(new RoundRectShape(r, null, null));
            return drawable;
        } else {
            return listDrawable;
        }
    }

    private Drawable getDividerDrawable() {
        ShapeDrawable drawable = new ShapeDrawable();

        if (!"".equals(divider_color)) {
            drawable.getPaint().setColor(Color.parseColor(divider_color));
        } else {
            drawable.getPaint().setColor(Color.parseColor("#FFFFFF"));
        }

        drawable.setShape(new RoundRectShape(r, null, null));

        return drawable;
    }

    private void setAdapter(PopupListItemAdapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            Log.d("dropDown", "adapter not null....");
            setListView();
            lv.setAdapter(this.adapter);

            etSearch.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    DropDownView.this.adapter.applyFilter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });
            selectedPosition = 0;
        } else {
            selectedPosition = -1;
            this.adapter = null;
        }
    }

    private void setListView() {
        if (ll == null) {
            ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.removeAllViews();

            etSearch = new EditText(mContext);
            etSearch.setHeight(ConversionUtil.INSTANCE.dpToPx(38, mContext));
            etSearch.setMaxLines(1);
            etSearch.setTextSize(getResources().getDimension(R.dimen.bodyTextSizeNormal));
            //etSearch.setTextSize(AndroidUtility.Companion.dpToPx(mContext,8));
            etSearch.setHint("Search");
            etSearch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            ll.addView(etSearch);
        }


        if (lv == null) {
            lv = new ListView(getContext());
            lv.setFooterDividersEnabled(false);
            Log.d("dropDown", "ListView null....");
            lv.setCacheColorHint(0);

            // lv.setBackgroundDrawable(listDrawable);
            lv.setDividerHeight(1);

            Drawable dividerDrawable = getDividerDrawable();
            if (dividerDrawable != null) {
                lv.setDivider(dividerDrawable);
            }

            // lv.setDivider(getResources().getDrawable(R.drawable.line_orange));
            lv.setOnItemClickListener(this);
            lv.setOnItemSelectedListener(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(4, 0, 4, 4);

            ll.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams lvParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lvParam.setMargins(0, 0, 0, 0);
            lv.setLayoutParams(lvParam);

            ll.addView(lv);
        }

    }

    private void refreshView() {
        if (adapter != null) {
            if (selectedPosition >= 0) {
                DropDownItem popupListItem = adapter.getItem(selectedPosition);
                setText(popupListItem.getTitle());
            }
        }
    }

    public void onClick(View v) {
        if (pw == null || !pw.isShowing()) {
            // popupWidth = getMeasuredWidth();
            if (lv != null) {
                pw = new PopupWindow(v);
                ll.setPadding(4, 0, 4, 4);

                reinitiatePW();

                pw.setContentView(ll);


                if (popupWidth == 0)
                    popupWidth = getMeasuredWidth();

                //System.out.println("onClick popupWidth " + popupWidth);
                //System.out.println("onClick popupHeight " + popupHeight);


                pw.setWidth(popupWidth);
                pw.setHeight(popupHeight);
                // pw.setBackgroundDrawable(new BitmapDrawable());
                //pw.setBackgroundDrawable(listDrawable);
                pw.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                pw.setOutsideTouchable(false);
                pw.setFocusable(true);
                pw.setClippingEnabled(true);
                pw.showAsDropDown(v, v.getScrollX(), v.getScrollY());
                //pw.showAtLocation(v, Gravity.BOTTOM, v.getScrollX(), v.getScrollY());
                pw.setOnDismissListener(this);
            }

        }

        if (onClickListener != null)
            onClickListener.onClick(v);
    }

    private void reinitiatePW() {
        listDrawable = getRoundDrawable();
        pw.setBackgroundDrawable(listDrawable);

        if (lv != null) {
            Drawable dividerDrawable = getDividerDrawable();
            if (dividerDrawable != null) {
                lv.setDivider(dividerDrawable);
                lv.setDividerHeight(1);
            }
            lv.setHeaderDividersEnabled(true);
            lv.setFooterDividersEnabled(true);
        }

    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int selectedPosition, long arg3) {
        if (pw != null)
            pw.dismiss();
        this.selectedPosition = selectedPosition;
        refreshView();
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(arg0, arg1, selectedPosition, arg3);
        onSelected(adapter.getItem(selectedPosition));
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        onSelected(adapter.getItem(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class PopupListItemAdapter extends ArrayAdapter<DropDownItem> {

        private List<DropDownItem> objectList;
        private List<DropDownItem> originalList;

        public PopupListItemAdapter(Context context, List<DropDownItem> objects) {
            super(context, android.R.layout.activity_list_item);
            objectList = objects;
            originalList = new ArrayList<>();
            originalList.addAll(objectList);
        }

        @Nullable
        @Override
        public DropDownItem getItem(int position) {
            return objectList.get(position);
        }

        @Override
        public int getCount() {
            return objectList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.drop_menu_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            DropDownItem menuItem = objectList.get(position);
            viewHolder.tvText.setText(menuItem.getTitle());
            Drawable resDrawable = menuItem.getItemIcon();

            viewHolder.tvText.setTextColor(Color.parseColor(tv_color));

            if (resDrawable != null) {
                viewHolder.ivIcon.setBackgroundDrawable(menuItem.getItemIcon());
            } else {
                viewHolder.ivIcon.setVisibility(View.GONE);
            }

            return convertView;
        }

        public void applyFilter(CharSequence constraint) {
            List<DropDownItem> list = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                // set the Original result to return
                objectList.clear();
                objectList.addAll(originalList);
            } else {
                String text = constraint.toString().toLowerCase();
                // set the Filtered result to return
                for (int i = 0; i < originalList.size(); i++) {
                    DropDownItem data = originalList.get(i);
                    if (data.getTitle().toLowerCase().startsWith(text)) {
                        list.add(data);
                    }
                }
                objectList.clear();
                objectList.addAll(list);
            }

            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView tvText;
            ImageView ivIcon;

            public ViewHolder(View v) {
                tvText = v.findViewById(R.id.tv_DropMenuTitle);
                ivIcon = v.findViewById(R.id.iv_DropMenuIcon);
            }

        }
    }

    public void hideSearch() {
        if (etSearch != null) {
            etSearch.setVisibility(View.GONE);
        }
    }

    public void setItems(List<DropDownItem> itemList) {
        if (itemList == null)
            throw new NullPointerException("Items Array is null.");
        if (list == null)
            list = new ArrayList();
        list.clear();
        for (DropDownItem item : itemList) {
            list.add(item);
        }
        adapter = new PopupListItemAdapter(getContext(), list);
        setAdapter(adapter);

    }


    public void onDismiss() {
        pw = null;
    }


    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(
            OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        refreshView();
    }


}
