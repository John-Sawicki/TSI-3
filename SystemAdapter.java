package com.example.android.tsi.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.tsi.R;

public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.SystemAdapterVH> {
    private String[][] mSystemNamed2D={{"PAGA Sound Coverage", "Wire Gauge Calculator", "Power Load Calculator", "Task Tracker"},
            {"Calculate speaker coverage diameter", "Calculate wire gauge based on distance",
             "Calculate UPS, PDU, and breaker sizes","Record Tasks by system and date"}};
    private boolean mLndscape = false;
    final private SystemOnClickInterface mSystemOnClickInterface;
    public SystemAdapter(SystemOnClickInterface clickInterface, boolean landscape){
        Log.d("SystemAdapter", "boolean "+landscape);
        mLndscape =landscape;
        mSystemOnClickInterface = clickInterface;
    }
    public interface SystemOnClickInterface{
        void onClick(int index);
    }
    @Override
    public SystemAdapter.SystemAdapterVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int systemLayout = R.layout.system_name;
        LayoutInflater layoutInflater =LayoutInflater.from(context);
        View view = layoutInflater.inflate(systemLayout, parent, false);
        return new SystemAdapterVH(view);
    }
    @Override
    public void onBindViewHolder(SystemAdapterVH holder, int position) {
        String systemName = mSystemNamed2D[0][position];
        String systemDescription = mSystemNamed2D[1][position];
        holder.tv_system_name.setText(systemName);
        holder.tv_system_description.setText(systemDescription);
    }
    @Override
    public int getItemCount() {
        return 4;
    }
    public class SystemAdapterVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tv_system_name,tv_system_description;
        public SystemAdapterVH(View view){
            super(view);
            tv_system_name = view.findViewById(R.id.tv_system_name);
            tv_system_description = view.findViewById(R.id.tv_system_description);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int systemClicked = getAdapterPosition();
            mSystemOnClickInterface.onClick(systemClicked);
        }
    }
}
