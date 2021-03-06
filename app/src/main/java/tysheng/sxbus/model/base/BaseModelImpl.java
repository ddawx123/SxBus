package tysheng.sxbus.model.base;

import android.app.Application;

import javax.inject.Inject;

import tysheng.sxbus.AppLike;
import tysheng.sxbus.db.StarHelper;
import tysheng.sxbus.di.component.DaggerUniverseComponent;
import tysheng.sxbus.net.BusService;

/**
 * Created by tysheng
 * Date: 2017/5/4 16:45.
 * Email: tyshengsx@gmail.com
 */

public class BaseModelImpl implements BaseModel {
    @Inject
    protected StarHelper mHelper;

    @Inject
    protected BusService mBusService;

    @Inject
    protected Application mApplication;

    public BaseModelImpl() {
        DaggerUniverseComponent.builder()
                .applicationComponent(AppLike.getAppLike().getApplicationComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onDestroy() {

    }
}
