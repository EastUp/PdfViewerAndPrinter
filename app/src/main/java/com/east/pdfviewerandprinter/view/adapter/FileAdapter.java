package com.east.pdfviewerandprinter.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.east.pdfviewerandprinter.R;

import java.io.File;


/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 * @description: 文件选择的Adapter
 * @author: jamin
 * @date: 2020/4/21 16:30
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_HOLDER_TYPE_NORMAL = 1;
    public static final int VIEW_HOLDER_TYPE_PARENT_DIR = 2;

    private OnItemClickListener itemClickListener;
    private SortedList<File> fileList;

    public interface OnItemClickListener {
        void onItemClicked(View view, File file, int itemType);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setFileList(SortedList<File> fileList) {
        /*如果不这么做的话，RecyclerViewDataObserver观察的永远是之前fileList的堆内存里的数据，
          即使重新 this.fileList = fileList; 附了值但是 RecyclerViewDataObserve还是指向最开始堆内存中的数据*/
        if(this.fileList == null)
            this.fileList = fileList;
        else{
            this.fileList.clear();
            for(int i=0; i< fileList.size() ; i++)
                this.fileList.add(fileList.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_file, parent, false);
        if (viewType == VIEW_HOLDER_TYPE_NORMAL) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClicked(view, (File) view.getTag(), VIEW_HOLDER_TYPE_NORMAL);
                    }
                }
            });
            return new NormalFileItemViewHolder(view);
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClicked(view, (File) view.getTag(), VIEW_HOLDER_TYPE_PARENT_DIR);
                    }
                }
            });
            return new ParentDirViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalFileItemViewHolder) {
            NormalFileItemViewHolder normalFileItemViewHolder = (NormalFileItemViewHolder) holder;
            File file = getItem(position);
            normalFileItemViewHolder.bindData(file);
            normalFileItemViewHolder.itemView.setTag(file);
        } else if (holder instanceof ParentDirViewHolder) {
            ParentDirViewHolder parentDirViewHolder = (ParentDirViewHolder) holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_HOLDER_TYPE_PARENT_DIR;
        } else {
            return VIEW_HOLDER_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (fileList == null) {
            return 0;
        }

        return fileList.size() + 1;
    }

    @Nullable
    protected File getItem(int position) {
        if (position == 0) {
            return null;
        }
        return fileList.get(position - 1);
    }

    static class NormalFileItemViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        ImageView fileIconImageView;

        public NormalFileItemViewHolder(View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.list_item_file_name);
            fileIconImageView = itemView.findViewById(R.id.list_item_file_icon);
        }

        public void bindData(@NonNull File file) {
            if (file.isFile()) {
                fileIconImageView.setImageResource(R.drawable.ic_file);
            } else {
                fileIconImageView.setImageResource(R.drawable.ic_folder);
            }
            fileNameTextView.setText(file.getName());
        }
    }

    static class ParentDirViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        ImageView fileIconImageView;

        public ParentDirViewHolder(View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.list_item_file_name);
            fileIconImageView = itemView.findViewById(R.id.list_item_file_icon);
            fileIconImageView.setImageResource(R.drawable.ic_folder);
            fileNameTextView.setText("..");
        }
    }
}
