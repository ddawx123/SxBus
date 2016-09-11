package tysheng.sxbus.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tysheng.sxbus.R;
import tysheng.sxbus.bean.Star;

/**
 * Created by Sty
 * Date: 16/8/10 22:07.
 */
public class StarAdapter extends BaseQuickAdapter<Star> {
    public StarAdapter(List<Star> data) {
        super(R.layout.item_star, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Star result) {
        holder.setText(R.id.textView, result.startStationName + " - " + result.endStationName)
                .setText(R.id.number, result.lineName)
                .addOnClickListener(R.id.textView)
                .addOnClickListener(R.id.star);
    }


}