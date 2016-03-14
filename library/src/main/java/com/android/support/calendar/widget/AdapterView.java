package com.android.support.calendar.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.support.calendar.R;
import com.android.support.calendar.exception.IllegalViewArgumentException;
import com.android.support.calendar.model.DayTime;
import com.android.support.calendar.util.CalendarUtility;

import java.util.Calendar;
import java.util.List;

import static com.android.support.calendar.exception.IllegalViewArgumentException.*;

/**
 * The View that contains the Adapter with the month data.
 *
 * @author jonatan.salas
 */
public class AdapterView extends LinearLayout {
    private OnListItemLongClickListener onListItemLongClickListener;
    private OnListItemClickListener onListItemClickListener;
    private DayTimeAdapter monthAdapter;
    private RecyclerView recyclerView;
    private View view;

    /**
     * Constructor with arguments. It only takes the Context as param.
     *
     * @param context the context used to inflate or get resources
     */
    public AdapterView(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor with arguments. It takes the Context as main param
     * and as second param an AttributeSet.
     *
     * @param context the context used to inflate or get resources
     * @param attrs   the attributes styled from a XML file
     */
    public AdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor with arguments. It takes the Context as main param
     * and as second param an AttributeSet, and as third param the defStyleAttr.
     *
     * @param context      the context used to inflate or get resources
     * @param attrs        the attributes styled from a XML file
     * @param defStyleAttr int resource used to get the Styles array
     */
    public AdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view, this, true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.invalidate();
    }

    public void init(@NonNull Calendar calendar, int monthIndex) {
        final List<DayTime> list = CalendarUtility.obtainDayTimes(calendar, monthIndex);

        if (null != monthAdapter) {
            monthAdapter.setItems(list);
        } else {
            monthAdapter = new DayTimeAdapter(list);
        }

        recyclerView.setAdapter(monthAdapter);
        monthAdapter.notifyDataSetChanged();

        recyclerView.invalidate();
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemSelected) {
        this.onListItemClickListener = onListItemSelected;
        invalidate();
    }

    public void setOnListItemLongClickListener(OnListItemLongClickListener onListItemSelected) {
        this.onListItemLongClickListener = onListItemSelected;
        invalidate();
    }

    /**
     * @author jonatan.salas
     */
    private class DayTimeAdapter extends RecyclerView.Adapter<DayTimeAdapter.DayTimeViewHolder> {
        private List<DayTime> items;

        public DayTimeAdapter(@NonNull List<DayTime> items) {
            this.items = items;
        }

        @Override
        public DayTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View v = LayoutInflater.from(getContext()).inflate(R.layout.day_view, parent, false);
            return new DayTimeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DayTimeViewHolder holder, int position) {
            final DayTime dayTime = items.get(position);

            holder.textView.setClickable(true);
            holder.textView.setText(String.valueOf(dayTime.getDay()));
//            holder.textView.setTextSize(mAdapterViewFontSize);
//            holder.textView.setTextColor(mAdapterViewTextColor);

            if (!dayTime.isCurrentMonth()) {
                holder.textView.setTypeface(Typeface.DEFAULT_BOLD);
//                holder.textView.setBackgroundColor(mDisabledBackgroundColor);
//                holder.textView.setTextColor(mDisabledTextColor);
                holder.textView.setEnabled(false);
                holder.textView.setClickable(false);
            }

            if (dayTime.isWeekend() && dayTime.isCurrentMonth()) {
//                holder.mDayView.setBackgroundColor(mWeekendBackgroundColor);
//                holder.textView.setTextColor(mWeekendTextColor);
                holder.textView.setTypeface(Typeface.DEFAULT_BOLD);
                holder.textView.setEnabled(true);
                holder.textView.setClickable(true);
            }

            if (dayTime.isCurrentDay() && dayTime.isCurrentMonth()) {
//                holder.textView.setBackgroundColor(mCurrentBackgroundColor);"onListItemClickListener can't be null!"
//                holder.textView.setTextColor(mCurrentTextColor);
                holder.textView.setEnabled(true);
                holder.textView.setClickable(true);
            }

            holder.textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onListItemClickListener) {
                        onListItemClickListener.onListItemClick(v, dayTime);
                    } else {
                        throw new IllegalViewArgumentException(ITEM_SELECTED_LISTENER_NOT_NULL_MESSAGE);
                    }
                }
            });

            holder.textView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean result = (null != onListItemLongClickListener);

                    if (result) {
                        onListItemLongClickListener.onListItemLongClick(v, dayTime);
                    } else {
                        throw new IllegalViewArgumentException(ITEM_LONG_SELECTED_LISTENER_NOT_NULL_MESSAGE);
                    }

                    return result;
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != items) ? items.size() : 0;
        }

        public void setItems(List<DayTime> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        protected class DayTimeViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public DayTimeViewHolder(@NonNull View view) {
                super(view);
                this.textView = (TextView) view.findViewById(R.id.day_view);
            }
        }
    }

    /**
     * @author jonatan.salas
     */
    public interface OnListItemClickListener {

        void onListItemClick(@NonNull View view, @NonNull DayTime dayTime);
    }

    /**
     * @author jonatan.salas
     */
    public interface OnListItemLongClickListener {

        void onListItemLongClick(@NonNull View view, @NonNull DayTime dayTime);
    }
}
