/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.chatuidemo.ui;

import android.os.Bundle;

import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.pgyersdk.crash.PgyCrashManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HxBaseActivity extends EaseBaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EventBus.getDefault().register(this);
        PgyCrashManager.register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        //PgyFeedbackShakeManager.setShakingThreshold(1000);
        //FeedbackActivity.setBarImmersive(true);
        //PgyFeedbackShakeManager.register(this, false);
    }
    @Override
    protected void onPause() {
        //PgyFeedbackShakeManager.unregister();
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop(){
        super.onStop();
        DemoHelper.getInstance().setAppBadge();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PgyCrashManager.unregister();
    }
    @Subscribe
    public void onEventMainThread(Boolean b) {
    }

}
