package ru.ztrap.formattedtdittextsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import ru.ztrap.formattedtdittextsample.FormAdapter.OnStartDragListener;
import ru.ztrap.formattedtdittextsample.FormAdapter.onEditClickListener;
import ru.ztrap.formattedtdittextsample.helper.SimpleItemTouchHelperCallback;
import ru.ztrap.views.FormattedEditText;

public class MainActivity extends AppCompatActivity implements OnStartDragListener, onEditClickListener {

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FormAdapter adapter = new FormAdapter(this, this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void edit(FormattedEditText fet) {

    }
}
