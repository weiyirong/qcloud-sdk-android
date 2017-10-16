package com.tencent.qcloud.core.logger;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

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
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */
class RecordLog {

    private String flag = null;

    public static final String LOG_DIR = "QCloudLogs";

    private static final int MAX_FILE_SIZE =  32* 1024; // Log文件大小
    private static final int MAX_FILE_COUNT = 30; // Log分片文件数量
    private static final long LOG_FLUSH_DURATION = 10 * 1000; // Log Buffer的flush周期
    private static final long BUFFER_SIZE = 32 * 1024; // Log内存缓冲大小

    //log根目录
    private String logRootDir = null;

    //文件过滤器
    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String filename = pathname.getName();
            boolean result = filename.endsWith("." + flag + ".log");
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
    private Handler handler = null;
    //buffer
    private List<Record> bufferRecord = Collections.synchronizedList(new ArrayList<Record>());
    private volatile long mBufferSize = 0;

    private static final byte[] object = new byte[0];
    private static final byte[] instance = new byte[0];
    private static RecordLog recordLog =null;

    private RecordLog(Context context, String flag){
        this.flag = flag;
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

    public static RecordLog getInstance(Context context, String flag){
        synchronized (instance){
            if(recordLog == null){
                recordLog = new RecordLog(context,flag);
            }
            return  recordLog;
        }
    }

    public String getLogRootDir(){
        return logRootDir;
    }

    //获取时间格式
    public String getTodayDate(){
        String simple_date_formate = "yyyy-MM-dd";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simple_date_formate, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public String getLongDate(long times){
        String simple_date_formate = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simple_date_formate,Locale.getDefault());
        return simpleDateFormat.format(times);
    }

    //log文件夹
    public String getLogFileDir(){
        String dir = logRootDir + File.separator + getTodayDate();
        File file = new File(dir);
        if(!file.exists()){
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file.getPath();
    }

    //log 文件(最新的）
    private File getLogFile(long times){

        String dirName =logRootDir + File.separator + getLongDate(times);
        //log文件夹是否存在
        File file = new File(dirName);
        if(!file.exists()){
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
            return new File(dirName,"1" + "." + this.flag + ".log");
        }else{
            File[] fileslist = file.listFiles(fileFilter);
            if(fileslist == null || fileslist.length == 0){
                return new File(dirName,"1" + "." + this.flag + ".log");
            }
            //得到最新的文件分片
            Arrays.sort(fileslist, new Comparator< File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return getIndexFromFile(lhs)-getIndexFromFile(rhs);
                }
            });
            File last = fileslist[fileslist.length -1];
            if(last.length() > RecordLog.MAX_FILE_SIZE ){
                int newIndex = getIndexFromFile(last) + 1;
                last = new File(dirName,""+ newIndex + "." + this.flag + ".log");
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
    public int getIndexFromFile(File file){
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
    public void write(List<Record> listInfo){
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

    //对外提供的接口
    public synchronized void appendRecord(String tag, RecordLevel level, String msg, Throwable t ){
        Record r = new Record(tag,level, msg, t);
        bufferRecord.add(r);
        mBufferSize += r.getLength();
        //有消息进入，发送一个通知
        Message message = handler.obtainMessage();
        message.what = MSG_FLUSH_CONTENT;
        handler.sendMessage(message);
    }

    public synchronized void flush(){
        if(mBufferSize <= 0)return;
        write(bufferRecord);
        bufferRecord.clear();
        mBufferSize = 0;
    }

    public synchronized void input(){
        if(mBufferSize > BUFFER_SIZE ){
            flush();
        }
    }
}
