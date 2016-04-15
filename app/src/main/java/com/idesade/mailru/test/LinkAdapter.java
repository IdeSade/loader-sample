package com.idesade.mailru.test;

import android.annotation.SuppressLint;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ItemHolder> {

    public interface OnItemClickListener {
        void onItemClick(LinkItem item);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mRootView;
        private TextView mLink;
        private LinkItem mLinkItem;
        private OnItemClickListener mClickListener;

        public ItemHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            mRootView = itemView;

            itemView.setOnClickListener(this);
            mClickListener = listener;

            mLink = (TextView) itemView.findViewById(R.id.link);
        }

        @SuppressLint("SetTextI18n")
        public void bind(LinkItem item) {
            int one = mRootView.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
            mRootView.setPadding(one * item.getLevel(), 0, 0, 0);

            mLinkItem = item;
            mLink.setText(item.getId() + "\n" +
                    item.getTitle() + "\n" +
                    item.getDisplayUrl());
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(mLinkItem);
            }
        }
    }

    private final SortedList<LinkItem> mData;
    private OnItemClickListener mOnItemClickListener;

    public LinkAdapter(OnItemClickListener listener) {
        mOnItemClickListener = listener;

        mData = new SortedList<>(LinkItem.class, new SortedListAdapterCallback<LinkItem>(this) {
            @Override
            public int compare(LinkItem o1, LinkItem o2) {
                return o1.compareId(o2);
            }

            @Override
            public boolean areContentsTheSame(LinkItem oldItem, LinkItem newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(LinkItem item1, LinkItem item2) {
                return TextUtils.equals(item1.getId(), item2.getId());
            }
        });
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addAll(Collection<LinkItem> items) {
        mData.addAll(items);
    }
}
