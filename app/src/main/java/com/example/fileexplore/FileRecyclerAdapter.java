package com.example.fileexplore;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileRecyclerAdapter extends BaseRecyclerAdapter<FileRecyclerAdapter.CustomViewHolder,FileItem> {


    public FileRecyclerAdapter(List<FileItem> objectList, RecyclerClickListener listener) {
        super(objectList,listener);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item,parent,false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        FileItem entity = getItem(position);

        holder.fileHeader.setText(entity.getFile());
        String iconText = entity.isDirectory() ? "D" : "F";
        holder.iconText.setText(iconText);
//        holder.fileSubHeader.setText(entity.getSubHeader());

        holder.parentLayout.setTag(position);
        holder.parentLayout.setOnClickListener(this);

    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView iconText,fileHeader,fileSubHeader;
        ViewGroup parentLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            iconText = itemView.findViewById(R.id.icon_text);
            fileHeader = itemView.findViewById(R.id.file_header);
            fileSubHeader = itemView.findViewById(R.id.file_sub_header);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
