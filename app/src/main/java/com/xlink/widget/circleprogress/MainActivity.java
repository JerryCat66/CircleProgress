package com.xlink.widget.circleprogress;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xlink.widget.circleprogresslibrary.CircleProgress;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private CircleProgress mProgress;
    public TextView mTvShowMsg;
    public Handler mHandler;

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mWeakReference;

        public MyHandler(MainActivity activity) {
            mWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mWeakReference.get();
            super.handleMessage(msg);
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.mTvShowMsg.setText("执行动作1");
                        break;
                    case 2:
                        activity.mTvShowMsg.setText("执行动作2");
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = findViewById(R.id.circle);
        mTvShowMsg = findViewById(R.id.tv_show);
        mProgress.setMin(0);
        mProgress.setMax(100);
        mProgress.setProgress(30);

        mHandler = new MyHandler(this);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        }.start();
        //创建定长线程池,长度为5,适用于要控制线程并发数量的场景
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        //创建一个任务
        Runnable fixedTask = new Runnable() {
            @Override
            public void run() {
                System.out.print("执行一个定长线程池中的任务");
            }
        };
        //放入线程池
        fixedThreadPool.execute(fixedTask);
        //关闭线程池
        fixedThreadPool.shutdown();

        //创建定时线程池,适合需要执行定时任务的线程
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

        //创建一个任务
        Runnable scheduleTask = new Runnable() {
            @Override
            public void run() {
                System.out.print("执行一个定时线程池中的任务");
            }
        };

        scheduledThreadPool.schedule(scheduleTask, 1, TimeUnit.SECONDS);//延迟一秒执行
        scheduledThreadPool.scheduleAtFixedRate(scheduleTask, 10, 1000, TimeUnit.MILLISECONDS);//延迟10毫秒之后每隔1000毫秒执行任务

        //关闭定时线程池
        scheduledThreadPool.shutdown();


        //创建可缓存的线程池，适用于大量且耗时少的任务
        ExecutorService cacheThreadPool = Executors.newCachedThreadPool();
        Runnable cacheTask = new Runnable() {
            @Override
            public void run() {
                System.out.print("执行一个缓存线程池中的任务");
            }
        };

        cacheThreadPool.execute(cacheTask);

        cacheThreadPool.shutdown();


        //创建单线程化线程池,适用于单线程，不用考虑线程同步问题
        ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

        Runnable singleTask = new Runnable() {
            @Override
            public void run() {
                System.out.print("执行一个单线程化线程池中的任务");
            }
        };

        singleThreadPool.execute(singleTask);

        singleThreadPool.shutdown();

    }

}
