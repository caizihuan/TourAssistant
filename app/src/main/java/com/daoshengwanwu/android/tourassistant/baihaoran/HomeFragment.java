package com.daoshengwanwu.android.tourassistant.baihaoran;


import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daoshengwanwu.android.tourassistant.R;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private LinearLayout mAreaSelBtn;
    private EditText mSearchEditText;
    private ImageView mLocImg;
    private LinearLayout mHideShow;
    private LinearLayout mScrollShow;
    private Banner mBanner;
    private RecyclerView mRecyclerView;
    private List<Spot> mListData;
    private ScrollView mScrollView;
    private RelativeLayout mTitle;
    private CustomerLinearLayoutManager mLinearLayoutManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            judgeShouldSwitch();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.baihaoran_fragment_home, container, false);

        getWidgetsReferences(v);
        initView();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    mHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        return v;
    }

    private void getWidgetsReferences(View v) {
        mAreaSelBtn = (LinearLayout)v.findViewById(R.id.bhr_home_area_select_button);
        mSearchEditText = (EditText)v.findViewById(R.id.bhr_home_title_edit_text);
        mLocImg = (ImageView)v.findViewById(R.id.bhr_home_loc_img);
        mHideShow = (LinearLayout)v.findViewById(R.id.bhr_home_hide_show_linear_layout);
        mBanner = (Banner)v.findViewById(R.id.bhr_home_banner);
        mScrollShow = (LinearLayout)v.findViewById(R.id.bhr_home_scroll_linear_layout);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.bhr_home_area_recommand_rv);
        mListData = HomeModel.getInstance().getSpots();
        mScrollView = (ScrollView)v.findViewById(R.id.bhr_home_scroll_view);
        mTitle = (RelativeLayout)v.findViewById(R.id.bhr_home_title_relative_layout);
        mLinearLayoutManager = new CustomerLinearLayoutManager(getActivity());
    }

    private void initView() {
        //获取文字高度
        float lineHeight = DisplayUtil.getTextHeight(getActivity(), 12.0f);

        //设置下拉图标高度
        ImageView img = (ImageView)mAreaSelBtn.findViewById(R.id.bhr_home_area_img);
        ViewGroup.LayoutParams params = img.getLayoutParams();
        params.height = (int)(lineHeight * 0.8);
        params.width = (int)(lineHeight * 0.8);
        img.setLayoutParams(params);

        //设置定位图标高度
        params = mLocImg.getLayoutParams();
        params.height = (int)(lineHeight * 1.6);
        params.width = (int)(lineHeight * 1.6);
        mLocImg.setLayoutParams(params);

        //设置搜索栏搜索图标的高度
        Drawable drawStart = ContextCompat.getDrawable(getActivity(), R.drawable.search);
        int drawHNW = (int)(lineHeight);
        drawStart.setBounds(0, 0, drawHNW, drawHNW);
        mSearchEditText.setCompoundDrawables(drawStart, null, null, null);

        //设置hide-show中drawableEnd
        lineHeight = DisplayUtil.getTextHeight(getActivity(), 18.0f);
        Drawable drawEnd = ContextCompat.getDrawable(getActivity(), R.drawable.like);
        drawHNW = (int)(lineHeight * 0.8);
        drawEnd.setBounds(0, 0, drawHNW, drawHNW);
        TextView hideShowTV = (TextView)(mHideShow.findViewById(R.id.bhr_home_city_recommond_hide_show_tv));
        hideShowTV.setCompoundDrawables(null, null, drawEnd, null);

        //设置Banner的高度
        WindowManager winManager = getActivity().getWindowManager();
        Point point = new Point();
        winManager.getDefaultDisplay().getRealSize(point);
        int screenWidth = point.x;
        int height = (int)(9.0 / 16.0 * screenWidth);
        params = mBanner.getLayoutParams();
        params.height = height;
        mBanner.setLayoutParams(params);

        //设置Banner轮播图
        mBanner.setImageLoader(new GlideImageLoader());
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.banner_1);
        images.add(R.drawable.banner_2);
        mBanner.setImages(images);
        mBanner.start();

        //设置scroll的drawableEnd
        lineHeight = DisplayUtil.getTextHeight(getActivity(), 18.0f);
        drawEnd = ContextCompat.getDrawable(getActivity(), R.drawable.like);
        drawHNW = (int)(lineHeight * 0.8);
        drawEnd.setBounds(0, 0, drawHNW, drawHNW);
        TextView scrollShowTV = (TextView)(mScrollShow.findViewById(R.id.bhr_home_city_recommond_scroll_tv));
        scrollShowTV.setCompoundDrawables(null, null, drawEnd, null);

        //配置RecyclerView
        mLinearLayoutManager.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(new RecommandListAdapter());
        params = mRecyclerView.getLayoutParams();
        params.height = DisplayUtil.getScreenHeight(getActivity()) * 5;
        mRecyclerView.setLayoutParams(params);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                judgeShouldSwitch();
                return false;
            }
        });

        //为scrollView设置监听器
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                judgeShouldSwitch();
                return false;
            }
        });
    }

    private void judgeShouldSwitch() {
        int[] xy = new int[2];
        mScrollShow.getLocationOnScreen(xy);
        int showTop = xy[1];
        mTitle.getLocationOnScreen(xy);
        int titleTop = xy[1];
        int titleBottom = titleTop + mTitle.getHeight();

        if (showTop <= titleBottom) {
            mHideShow.setVisibility(View.VISIBLE);
            mLinearLayoutManager.setScrollEnabled(true);
        } else {
            mHideShow.setVisibility(View.INVISIBLE);
            mLinearLayoutManager.setScrollEnabled(false);
        }
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    public class SpotHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImg;
        private TextView mSpotName;
        private TextView mSpotEnName;
        private TextView mDistance;


        public SpotHolder(View itemView) {
            super(itemView);

            mItemImg = (ImageView)itemView.findViewById(R.id.bhr_home_item_img);
            mSpotName = (TextView)itemView.findViewById(R.id.bhr_home_spot_name);
            mSpotEnName = (TextView)itemView.findViewById(R.id.bhr_home_en_spot_name);
            mDistance = (TextView)itemView.findViewById(R.id.bhr_home_distance);

            //获取手机屏幕宽度
            WindowManager winMan = getActivity().getWindowManager();
            Point size = new Point();
            winMan.getDefaultDisplay().getRealSize(size);
            int screenWidth = size.x;

            //求出图片应有的高度，并设置图片的高度
            int imgWidth = (int)(screenWidth * 2.0 / 5.0);
            int imgHeight = (int)(imgWidth * 9.0 / 16.0);
            ViewGroup.LayoutParams params = mItemImg.getLayoutParams();
            params.height = imgHeight;
            params.width = imgWidth;
            mItemImg.setLayoutParams(params);

            //设置整个item的高度
            params = itemView.getLayoutParams();
            params.height = imgHeight;
            itemView.setLayoutParams(params);

            //设置右边LinearLayout的宽度
            LinearLayout rLL = (LinearLayout)itemView.findViewById(R.id.bhr_home_list_item_right_ll);
            params = rLL.getLayoutParams();
            params.width = (int)(screenWidth * 3.0 / 5.0);
            rLL.setLayoutParams(params);
        }

        public void bindData(Spot spot) {
            mItemImg.setImageResource(spot.getDrawableResId());
            mSpotName.setText(spot.getSpotName());
            mSpotEnName.setText(spot.getSpotEnName());
            mDistance.setText("距离：" + spot.getDistance());
        }
    }

    public class RecommandListAdapter extends RecyclerView.Adapter<SpotHolder> {
        @Override
        public SpotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View item = inflater.inflate(R.layout.baihaoran_home_list_item, parent, false);

            return new SpotHolder(item);
        }

        @Override
        public void onBindViewHolder(SpotHolder holder, int position) {
            if (null != holder) {
                holder.bindData(mListData.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mListData.size();
        }
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }

    public class CustomerLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        private final String TAG = CustomerLinearLayoutManager.class.getSimpleName();


        public CustomerLinearLayoutManager(Context context) {
            super(context);
        }

        public CustomerLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }

        public void setScrollEnabled(boolean flag) {
            isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
