package info.zhegui.words;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class ExceptionHandler implements UncaughtExceptionHandler {
    private final String TAG = "ExceptionHandler";

    // 获取application 对象；
    private Context mContext;

    private UncaughtExceptionHandler defaultExceptionHandler;
    // 单例声明CustomException;
    private static ExceptionHandler customException;

    private static final String CRASH_REPORTER_EXTENSION = ".log";

    /**
     * 手机型号
     */
    private String phoneModelNumber;
    /**
     * 安卓版本
     */
    private String androidVersion;

    private ExceptionHandler() {

        phoneModelNumber = android.os.Build.MODEL;
        androidVersion = android.os.Build.VERSION.RELEASE;
    }

    public static ExceptionHandler getInstance() {
        if (customException == null) {
            customException = new ExceptionHandler();
        }

        return customException;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        // TODO Auto-generated method stub
        if (defaultExceptionHandler != null) {

            saveCrashInfoToFile(e);

            // 打印错误信息
            e.printStackTrace();


            // 将异常抛出，则应用会弹出异常对话框.这里先注释掉
            // defaultExceptionHandler.uncaughtException(thread, e);

            //直接退出
            System.exit(0);
        }
    }

    public void init(Context context) {
        mContext = context;
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private void saveCrashInfoToFile(Throwable ex) {

        Time t = new Time();
        t.setToNow(); // 取得系统时间

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String deviceInfoStr = "phoneModelNumber:" + phoneModelNumber
                + "\nandroidVersion:" + androidVersion;
        String result = info.toString() + "\n" + deviceInfoStr;
        printWriter.close();
        try {
            // long timestamp = System.currentTimeMillis();
            // int date = t.year * 10000 + t.month * 100 + t.monthDay;
            // int time = t.hour * 10000 + t.minute * 100 + t.second;

            // 保存至本应用内部存储
            // String fileName = "crash-" + date + "-" + time
            // + CRASH_REPORTER_EXTENSION;
//            String fileName = t.format("%Y-%m-%d_%H-%M-%S") + "_"
//                    + ex.getClass().getSimpleName() + ".log";
//            Log.i(TAG, "line 103--log file name:" + fileName);
//            FileOutputStream trace = mContext.openFileOutput(fileName,
//                    Context.MODE_PRIVATE);
//            trace.write(result.getBytes());
//
//            trace.flush();
//            trace.close();
            //


            FileOutputStream outStream = new FileOutputStream(
                    mContext.getExternalFilesDir("log").getAbsolutePath() + File.separator + "error.log"
                    , true);
            OutputStreamWriter writer = new OutputStreamWriter(outStream,
                    "utf-8");
            result += "time:" + t.format("%Y-%m-%d_%H:%M:%S") + "\n";
            result += "-------------------------------\n\n";
            writer.append(result);
            writer.flush();
            writer.close();// 记得关闭
            outStream.close();
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file...", e);
        }
    }
}