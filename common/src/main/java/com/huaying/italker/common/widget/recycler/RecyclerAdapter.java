package com.huaying.italker.common.widget.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huaying.italker.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressWarnings("JavaDoc")
public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {
    private final List<Data> mDataList;
    private AdapterListener<Data> mListener;

    public RecyclerAdapter() {
        this(null);
    }

    private RecyclerAdapter(AdapterListener<Data> listener){
        this(new ArrayList<Data>(),listener);
    }

    private RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener){
        this.mDataList = dataList;
        this.mListener = listener;
    }

    /**
     * 复写布局类型
     *
     * @param position 坐标
     * @return
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 获得布局类型
     *
     * @param position
     * @param data
     * @return
     */
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 创建一个viewholder
     *
     * @param parent   RecyclerView
     * @param viewType 界面类型,约定为xml布局id
     * @return ViewHolder
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewType) {
        //把xml初始化为view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //把xml id为viewType的布局文件初始化为一个root view（根布局）
        View root = inflater.inflate(viewType, parent, false);
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);

        //设置view tag为viewholder,双向绑定
        root.setTag(R.id.tag_recycler_holder,holder);

        //设置点击事件
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);


        //进行界面注解绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        //绑定callback
        holder.callback = this;
        return holder;
    }

    /**
     * 获得一个新的viewHolder
     *
     * @param root 根布局
     * @param viewType 布局类型，即布局文件xml的id
     * @return
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    /**
     * 绑定数据到一个holder上
     *
     * @param holder   holder
     * @param position 坐标
     */
    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {
        //获得要绑定的数据
        Data data = mDataList.get(position);
        //触发Holder的绑定方法
        holder.bind(data);
    }

    /**
     * 获得集合的数据量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 返回整个集合
     */
    public List<Data> getItems(){
        return mDataList;
    }

    /**
     * 插入一条数据并通知插入
     *
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入一堆数据，通知这段集合更新
     *
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeInserted(startPos, dataList.length);
        }
    }

    /**
     * 插入一堆数据，通知这段集合更新
     *
     * @param dataList
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    /**
     * 删除
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换为一新的集合，包括了清空
     *
     * @param dataList
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList != null && dataList.size() > 0) {
            mDataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    @Override
    public void update(Data data, ViewHolder<Data> holder) {
        //得到当前viewholder对应的坐标
        int pos = holder.getAdapterPosition();
        if (pos>=0){
            //数据移除更新
            mDataList.remove(pos);
            mDataList.add(pos,data);
            //通知这个坐标下的有更新
            notifyItemChanged(pos);
        }
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            //获得viewholder当前对应的adpater中的坐标
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onLongItemClick(viewHolder, mDataList.get(pos));
            return true;
        }

        return false;
    }

    /**
     * 设置适配器监听
     * @param adapterListener
     */
    public void setListener(AdapterListener<Data> adapterListener){
        this.mListener = adapterListener;
    }

    /**
     * DIY监听器
     *
     * @param <Data>
     */
    public interface AdapterListener<Data> {
        //cell点击触发
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);

        //cell长按触发
        void onLongItemClick(RecyclerAdapter.ViewHolder holder, Data data);
    }

    /**
     * 自定义viewholder
     * 新的ViewHolder将会被用来通过adapter调用onBindViewHolder展示item
     *
     * @param <Data> 泛型类型
     */
    @SuppressWarnings("WeakerAccess")
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
        protected Data mData;
        protected Unbinder unbinder;
        private AdapterCallback<Data> callback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 用于绑定数据时触发
         *
         * @param data 绑定的数据
         */
        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        /**
         * 当触发绑定数据的回调，必须复写
         *
         * @param data 绑定的数据
         */
        protected abstract void onBind(Data data);

        /**
         * holder自己对自己对应的data进行更新操作
         *
         * @param data
         */
        public void updateData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }

    public abstract static class AdapterListenerImpl<Data> implements AdapterListener<Data>{

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onLongItemClick(ViewHolder holder, Data data) {

        }
    }
}
