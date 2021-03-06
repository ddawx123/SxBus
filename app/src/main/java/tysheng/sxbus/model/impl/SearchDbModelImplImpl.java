package tysheng.sxbus.model.impl;

import java.util.List;

import tysheng.sxbus.Constant;
import tysheng.sxbus.bean.Star;
import tysheng.sxbus.dao.StarDao;
import tysheng.sxbus.model.base.BaseModel;
import tysheng.sxbus.model.base.BaseModelImpl;

/**
 * Created by tysheng
 * Date: 2017/1/3 14:42.
 * Email: tyshengsx@gmail.com
 */

public class SearchDbModelImplImpl extends BaseModelImpl implements BaseModel {

    public List<Star> getRecentList() {
        return mHelper.queryBuilder()
                .where(StarDao.Properties.TableName.eq(Constant.RECENT))
                .limit(20)
                .orderDesc(StarDao.Properties.MainId)
                .list();
    }

    public void onItemClick(Star star) {
        star.setTableName(Constant.RECENT);
        mHelper.saveOrUpdate(star);
    }

    public void onItemClickCollect(Star item) {
        item.isStar = true;

        Star uni = mHelper.queryBuilder()
                .where(StarDao.Properties.Id.eq(item.getId()), StarDao.Properties.TableName.eq(Constant.STAR))
                .unique();
        if (uni == null) {
            try {
                Star s = (Star) item.clone();
                s.setTableName(Constant.STAR);
                mHelper.saveOrUpdate(s);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete() {
        mHelper.delete(getRecentList());
    }
}
