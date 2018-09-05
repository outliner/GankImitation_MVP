package com.conan.gankimitation.view.activities;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.conan.gankimitation.R;
import com.conan.gankimitation.bean.GankList;
import com.conan.gankimitation.data.network.GankApi;
import com.conan.gankimitation.data.repository.IRepository;
import com.conan.gankimitation.databinding.WelfareLayoutBinding;
import com.conan.gankimitation.utils.AppUtil;
import com.conan.gankimitation.utils.Constants;
import com.conan.gankimitation.utils.LogUtil;
import com.conan.gankimitation.view.adapter.WelfareAdapter;
import com.conan.gankimitation.viewmodel.GankListViewModel;
import com.conan.gankimitation.viewmodel.ViewModelFactory;
import com.conan.gankimitation.widget.GankRecyclerView;
import com.conan.gankimitation.widget.WelfareItemDecoration;

import javax.inject.Inject;

/**
 * Description：福利Activity
 * Created by：JasmineBen
 * Time：2017/10/31
 */

public class WelfareActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,GankRecyclerView.OnLoadMoreListener{

    private static final String TAG = WelfareActivity.class.getSimpleName();

    @Inject
    WelfareAdapter mAdapter;
    @Inject
    IRepository mRepository;
    SwipeRefreshLayout mSwipeRefreshLayout;
    GankRecyclerView mRecyclerView;
    WelfareLayoutBinding mBinding;
    GankListViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }


    @Override
    protected void onNecessaryPermissionGranted() {
        getActivityComponent().inject(this);
        initViews();
    }

    private void initViews(){
        mBinding = DataBindingUtil.setContentView(this,R.layout.welfare_layout);
        mViewModel = obtainViewModel();
        mBinding.setViewmodel(mViewModel);
        mViewModel.getLoadMoreTaskEvent().observe(this, new Observer<GankList>() {
            @Override
            public void onChanged(@Nullable GankList gankList) {
                fetchWelfareListSuccess(gankList,false);
            }
        });
        mViewModel.getRefreshTaskEvent().observe(this, new Observer<GankList>() {
            @Override
            public void onChanged(@Nullable GankList gankList) {
                fetchWelfareListSuccess(gankList,true);
            }
        });
        customToolbar();
        initSwipeView();
        intRecyclerView();
        initData();
    }

    private void customToolbar(){
        Toolbar toolbar = mBinding.welfareToolbar.toolbar;
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolbar.setTitle(R.string.welfare);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i(TAG,"setNavigationOnClickListener");
                finish();
            }
        });
    }

    private void initSwipeView(){
        mSwipeRefreshLayout = mBinding.swipeLayout;
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void intRecyclerView() {
        mRecyclerView = mBinding.recyclerview;

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerView.addItemDecoration(new WelfareItemDecoration(16,2));
    }

    private void initData(){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        mViewModel.fetchGankList(GankApi.GankDataType.DATA_TYPE_WELFARE,1,Constants.PAGE_SIZE);
    }

    @Override
    public void onLoadMore() {
        if(!mSwipeRefreshLayout.isRefreshing()){
            int pageIndex = AppUtil.getPageIndex(mAdapter.getItemCount(), Constants.PAGE_SIZE);
            Log.i(TAG,"onLoadMore pageIndex:"+pageIndex);
            mViewModel.fetchGankList(GankApi.GankDataType.DATA_TYPE_WELFARE,pageIndex,Constants.PAGE_SIZE);
        }
    }

    public void fetchWelfareListSuccess(GankList gankList, boolean refresh) {
        if(gankList == null){
            fetchGankistComplete();
            return;
        }
        mAdapter.setData(gankList,refresh);
        Log.i("zpy","fetchWelfareListSuccess:"+mAdapter.getItemCount());
        fetchGankistComplete();
    }

    private void fetchGankistComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setLoadMoreComplete();
    }

    @Override
    protected void onDestroy() {
        mViewModel.onDestroy();
        LogUtil.i(TAG,"onDestroy");
        super.onDestroy();
    }

    private GankListViewModel obtainViewModel(){
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        GankListViewModel viewModel =  factory.create(GankListViewModel.class);
        viewModel.setRepository(mRepository);
        return viewModel;
    }
}
