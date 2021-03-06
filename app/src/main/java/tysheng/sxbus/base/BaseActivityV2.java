package tysheng.sxbus.base;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

import tysheng.sxbus.ui.base.BaseView;

/**
 * Created by tysheng
 * Date: 2017/3/12 12:54.
 * Email: tyshengsx@gmail.com
 */

public abstract class BaseActivityV2<Binding extends ViewDataBinding> extends BaseActivity implements BaseView {
    protected Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        initData(savedInstanceState);
    }

    @Override
    protected void normalCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public <R> LifecycleTransformer<R> bindUntilDestroyView() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public FragmentManager getChildFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    public View getRootView() {
        return binding.getRoot();
    }
}
