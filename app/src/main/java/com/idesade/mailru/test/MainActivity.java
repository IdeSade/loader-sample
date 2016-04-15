package com.idesade.mailru.test;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Collection<LinkItem>> {

    public static final int LOADER_SEARCH_ID = 0;
    public static final int LOADER_LINK_ID = 1;
    public static final int VISIBLE_THRESHOLD = 25;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LinkAdapter mAdapter;

    private LoaderManager mLoaderManager;

    private LinkItem mClickItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new LinkAdapter(new LinkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LinkItem item) {
                Log.d("123", "Click item! Id: " + item.getId());
                boolean isLoading = mLoaderManager.getLoader(LOADER_SEARCH_ID) != null;
                if (!isLoading) {
                    mClickItem = item;
                    mLoaderManager.initLoader(LOADER_LINK_ID, null, MainActivity.this).forceLoad();
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mAdapter.getItemCount();
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                boolean isLoading = mLoaderManager.getLoader(LOADER_SEARCH_ID) != null;
                boolean hasData = !TextUtils.isEmpty(MainApplication.getLinkData().getNextQuery());
                if (!isLoading && hasData && lastVisibleItem + VISIBLE_THRESHOLD > totalItemCount) {
                    mLoaderManager.initLoader(LOADER_SEARCH_ID, null, MainActivity.this).forceLoad();
                }
            }
        });

        mLoaderManager = getSupportLoaderManager();
        if (MainApplication.getLinkData().isEmpty()) {
            mLoaderManager.initLoader(LOADER_SEARCH_ID, null, this).forceLoad();
        } else {
            mAdapter.addAll(MainApplication.getLinkData().getData());
        }
    }

    @Override
    public Loader<Collection<LinkItem>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_SEARCH_ID: return new SearchResultLoader(this, MainApplication.getLinkData());
            case LOADER_LINK_ID: return new LinkLoader(this, mClickItem);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Collection<LinkItem>> loader, Collection<LinkItem> data) {
        mAdapter.addAll(data);
        getSupportLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Collection<LinkItem>> loader) {
    }
}
