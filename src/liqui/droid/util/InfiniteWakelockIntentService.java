package liqui.droid.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.os.PowerManager;

/**
 * This is a mix of Android's IntentService and Mark Murphy's
 * WakefulIntentService. In addition to doing work in a background thread and
 * acquiring a wakelock, it continues running until a the operation is finished.
 * 
 * @author Thomas Taschauer, tomtasche.at
 * @author Google, android.com
 * @author Mark Murphy, commonsware.com
 */
public abstract class InfiniteWakelockIntentService extends Service implements
        IdleHandler {

    private static final String NAME = "InfiniteWakelockIntentService";
    private static volatile PowerManager.WakeLock lockStatic = null;

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);

            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
            lockStatic.setReferenceCounted(true);
        }

        return (lockStatic);
    }

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj);
        }
    }

    public InfiniteWakelockIntentService(String name) {
        super();
        mName = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getLock(getApplicationContext()).acquire();

        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        mServiceHandler.post(new AddIdleHandlerRunnable());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ((flags & START_FLAG_REDELIVERY) != 0) {
            getLock(this.getApplicationContext()).acquire();
        }

        super.onStartCommand(intent, flags, startId);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        getLock(getApplicationContext()).release();

        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean queueIdle() {
        if (isFinished()) {
            stopSelf();
        } else {
            mServiceHandler.postDelayed(new AddIdleHandlerRunnable(), 2000);
        }

        return false;
    }

    protected abstract boolean isFinished();

    protected abstract void onHandleIntent(Intent intent);

    private class AddIdleHandlerRunnable implements Runnable {

        @Override
        public void run() {
            Looper.myQueue().addIdleHandler(InfiniteWakelockIntentService.this);
        }
    }
}