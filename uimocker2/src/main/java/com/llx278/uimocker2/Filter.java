package com.llx278.uimocker2;

import android.view.View;

/**
 * 这个接口主要用来判断一个view是否是你想要的
 * Created by llx on 03/01/2018.
 */

public interface Filter {
    /**
     * 传入的这个view是不是你想要的
     * @param view 传入的view
     * @return true 这个view是想要的，false 不是
     */
    boolean match(View view);
}
