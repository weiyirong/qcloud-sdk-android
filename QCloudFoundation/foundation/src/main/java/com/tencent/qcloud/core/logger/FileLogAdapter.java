package com.tencent.qcloud.core.logger;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * File Log Printer.
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
public class FileLogAdapter implements LogAdapter {

    private String alias;
    private int minPriority;

    private static final String LOG_DIR = "QCloudLogs";

    private static final int MAX_FILE_SIZE =  32* 1024; // Log文件大小
    private static final int MAX_FILE_COUNT = 30; // Log分片文件数量
    private static final long LOG_FLUSH_DURATION = 10 * 1000; // Log Buffer的flush周期
    private static final long BUFFER_SIZE = 32 * 1024; // Log内存缓冲大小

    //log根目录
    private String logRootDir;

    //文件过滤器
    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String filename = pathname.getName();
            boolean result = filename.endsWith("." + alias + ".log");
            if(!result){
                return  false;
            }
            result = getIndexFromFile(pathname)!=-1;
            return result;
        }
    };
    private static final int MSG_FLUSH_ALL = 0;
    private static final int MSG_FLUSH_CONTENT = 1;
    private static final int MSG_DELETE_FILE = 2;
    // 处理log者
    private Handler handler;
    //buffer
    private List<FileLogItem> bufferRecord = Collections.synchronizedList(new ArrayList<FileLogItem>());
    private volatile long mBufferSize = 0;

    private static final byte[] object = new byte[0];

    /**
     * 实例化一个文件日志记录器。默认写入 {@link QCloudLogger#INFO} 级别以上的日志。
     *
     * @param context 上下文
     * @param alias logger 文件别名
     */
    public FileLogAdapter(Context context, String alias){
        this(context, alias, QCloudLogger.INFO);
    }

    /**
     * 实例化一个文件日志记录器
     *
     * @param context 上下文
     * @param alias logger 文件别名
     * @param minPriority 最小的日志级别，低于这个级别的日志将不会被保存。
     */
    public FileLogAdapter(Context context, String alias, int minPriority){
        this.alias = alias;
        this.minPriority = minPriority;
        this.logRootDir = context.getExternalCacheDir() + File.separator + LOG_DIR;

        HandlerThread handlerThread = new HandlerThread("log_handlerThread", Thread.MIN_PRIORITY);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_FLUSH_ALL:
                        flush();
                        sendEmptyMessageDelayed(MSG_FLUSH_ALL,LOG_FLUSH_DURATION);
                        break;
                    case MSG_FLUSH_CONTENT:
                        input();
                        break;
                    case MSG_DELETE_FILE:
                        break;
                }
            }
        };
        Message message = handler.obtainMessage();
        message.what = MSG_FLUSH_ALL;
        handler.sendMessage(message);
    }

    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return priority >= minPriority;
    }

    @Override
    public synchronized void log(int priority, @NonNull String tag, @NonNull String message, @Nullable Throwable tr) {
        FileLogItem r = new FileLogItem(tag, priority, message, tr);
        bufferRecord.add(r);
        mBufferSize += r.getLength();
        //有消息进入，发送一个通知
        handler.removeMessages(MSG_FLUSH_CONTENT);
        handler.sendEmptyMessageDelayed(MSG_FLUSH_CONTENT, 500);
    }

    //获取时间格式
    private String getTodayDate(){
        String simple_date_formate = "yyyy-MM-dd";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simple_date_formate, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private String getLongDate(long times){
        String simple_date_formate = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simple_date_formate,Locale.getDefault());
        return simpleDateFormat.format(times);
    }

    //log 文件(最新的）
    private File getLogFile(long times){

        String dirName =logRootDir + File.separator + getLongDate(times);
        //log文件夹是否存在
        File file = new File(dirName);
        if(!file.exists()){
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
            return new File(dirName,"1" + "." + this.alias + ".log");
        }else{
            File[] fileslist = file.listFiles(fileFilter);
            if(fileslist == null || fileslist.length == 0){
                return new File(dirName,"1" + "." + this.alias + ".log");
            }
            //得到最新的文件分片
            Arrays.sort(fileslist, new Comparator< File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return getIndexFromFile(lhs)-getIndexFromFile(rhs);
                }
            });
            File last = fileslist[fileslist.length -1];
            if(last.length() > FileLogAdapter.MAX_FILE_SIZE ){
                int newIndex = getIndexFromFile(last) + 1;
                last = new File(dirName,""+ newIndex + "." + this.alias + ".log");
            }
            int filecounts = fileslist.length + 1;
            for(int i = 0; i< filecounts - MAX_FILE_COUNT; i++){
                //noinspection ResultOfMethodCallIgnored
                fileslist[i].delete();
            }
            return last;
        }
    }
    //获取文件分片的索引
    private int getIndexFromFile(File file){
        try{
            String filename = file.getName();
            int point = filename.indexOf('.');
            filename = filename.substring(0,point);
            return Integer.valueOf(filename);
        }catch (Exception e){
            e.printStackTrace();
            return  -1;
        }
    }
    //写入日志(同步)
    private void write(List<FileLogItem> listInfo){
        synchronized (object){
            if(listInfo == null) return;
            FileOutputStream fos = null;
            //noinspection TryWithIdenticalCatches
            try {
                File file = getLogFile(System.currentTimeMillis());
                fos = new FileOutputStream(file,true);
                for(int i = 0; i< listInfo.size(); i++){
                    fos.write(listInfo.get(i).toString().getBytes("UTF-8"));
                }
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private synchronized void flush(){
        if(mBufferSize <= 0)return;
        write(bufferRecord);
        bufferRecord.clear();
        mBufferSize = 0;
    }

    private synchronized void input(){
        if(mBufferSize > BUFFER_SIZE ){
            flush();
        }
    }
}
