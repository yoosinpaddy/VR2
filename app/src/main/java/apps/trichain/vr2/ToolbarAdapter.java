package apps.trichain.vr2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class ToolbarAdapter extends ArrayAdapter<Integer> {
    private Context context;
    private Integer[] icons;
    private static final String TAG = "ToolbarAdapter";
    public ToolbarAdapter(@NonNull Context context, Integer[] icons) {
        super(context, R.layout.toolbar_grid_cell, icons);
        Log.e(TAG, "updateGridActions: " );
        this.context = context;
        this.icons = icons;
    }


    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.toolbar_grid_cell, (ViewGroup) null);
        }
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(this.icons[i]);
        return view;
    }
}
