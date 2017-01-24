package ru.ztrap.formattedtdittextsample;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ztrap.formattedtdittextsample.helper.ItemTouchHelperAdapter;
import ru.ztrap.formattedtdittextsample.helper.ItemTouchHelperViewHolder;
import ru.ztrap.views.FormattedEditText;

/**
 * Created by zTrap on 24.01.2017.
 */

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {


    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }
    
    public interface onEditClickListener {
        void edit(FormattedEditText fet);
    }
    
    private final OnStartDragListener mDragStartListener;
    private final onEditClickListener onEditListener;
    private List<String> templates = new ArrayList<>();
    
    public FormAdapter(OnStartDragListener dragStartListener, onEditClickListener onEditListener) {
        mDragStartListener = dragStartListener;
        templates.add("+7 ({3}) {3}-{2}-{2}");
        this.onEditListener = onEditListener;
    }
    
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form, parent, false);
        return new ItemViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int p) {
        final int position = p;
        holder.template.setText(templates.get(position));
        holder.template.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                templates.set(position, s.toString());
            }
        });
        holder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fet.setFormat(holder.template.getText().toString());
            }
        });
        holder.apply.performClick();
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditListener.edit(holder.fet);
            }
        });
    
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return templates.size();
    }
    
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(templates, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
    
    @Override
    public void onItemDismiss(int position) {
        templates.remove(position);
        notifyItemRemoved(position);
    }
    
    static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        
        EditText template;
        FormattedEditText fet;
        Button apply;
        Button edit;
        
        ItemViewHolder(View itemView) {
            super(itemView);
            template = (EditText) itemView.findViewById(R.id.template);
            fet = (FormattedEditText) itemView.findViewById(R.id.fet);
            apply = (Button) itemView.findViewById(R.id.apply);
            edit = (Button) itemView.findViewById(R.id.edit);
        }
        
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }
        
        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
