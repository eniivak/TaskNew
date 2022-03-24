package com.example.tasknew;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AdapterListView extends BaseAdapter {
    private List<TareaModel> listViewItemDtoList = null;

    private Context ctx = null;

    public AdapterListView(Context ctx, List<TareaModel> listViewItemDtoList) {
        this.ctx = ctx;
        this.listViewItemDtoList = listViewItemDtoList;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(listViewItemDtoList!=null)
        {
            ret = listViewItemDtoList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int itemIndex) {
        Object ret = null;
        if(listViewItemDtoList!=null) {
            ret = listViewItemDtoList.get(itemIndex);
        }
        return ret;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {

        ListViewItemViewHolder viewHolder = null;

        if(convertView!=null)
        {
            viewHolder = (ListViewItemViewHolder) convertView.getTag();
        }else
        {
            convertView = View.inflate(ctx, R.layout.list_view_layout, null);

            CheckBox listItemCheckbox = (CheckBox) convertView.findViewById(R.id.checkbox);

            TextView listItemText = (TextView) convertView.findViewById(R.id.textView);

            viewHolder = new ListViewItemViewHolder(convertView);

            viewHolder.setItemCheckbox(listItemCheckbox);

            viewHolder.setItemTextView(listItemText);

            convertView.setTag(viewHolder);
        }

       TareaModel listViewItemDto = listViewItemDtoList.get(itemIndex);
       viewHolder.getItemCheckbox().setChecked(listViewItemDto.isChecked());
       viewHolder.getItemTextView().setText(listViewItemDto.getItemText());


        return convertView;
    }
}
