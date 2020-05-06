package apps.trichain.vr2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

public class MainActivity extends Activity {
    private static final String SKU_ID = "premium_upgrade";
    /* access modifiers changed from: private */
    public int H;
    private final int LIST = 0;
    private final int LOADED = 1;
    protected int MAX_COUNT_BEFORE_AD = 5;
    private int MAX_W;
    private int MIN_W;
    private final int PAUSED = 3;
    private final int PLAYING = 2;
    int STEPS = 5;
    private final int UNLOADED = -1;
    /* access modifiers changed from: private */
    public int W;
    boolean ad_requested = false;
    private ImageView btnPickVideo;
    private View clickGuard;
    Context context = this;
    private Bitmap defaultBitmap = null;
    Handler displayHandler = new Handler();
    private View dividerLine;
    boolean first_video = true;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetectorMain;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetectorPlay;
    private GridView gridActions;
    Handler hideToolbarHandler = new Handler();
    private ImageView imgBack;
    /* access modifiers changed from: private */
    public FrameDoublerView imgDoubler;
    /* access modifiers changed from: private */
    public boolean isFullScr;
    private boolean isIntent;
    /* access modifiers changed from: private */
    public boolean isLoop;
    boolean isPRO = false;
    boolean isPaused = false;
    /* access modifiers changed from: private */
    public boolean isTall;
    private long last_back_press_time = 0;
    private long last_request_time;
    private LinearLayout layoutAd;
    private LinearLayout layoutAdView;
    private RelativeLayout layoutMain;
    /* access modifiers changed from: private */
    public LinearLayout layoutProgress;
    private RelativeLayout layoutWebView;
//    AdView mAdView = null;
//    private BillingClient mBillingClient;
    /* access modifiers changed from: private */
    public int mCurrPerc = 0;
    int mCurrTime = 0;
    int mDuration = 0;
    /* access modifiers changed from: private */
    public int mHeight;
    private Handler mHideUiHandler = new Handler();
    int mMaxWidth = 0;
    int mMinWidth = 0;
    /* access modifiers changed from: private */
    public String mQuality;
    /* access modifiers changed from: private */
    public int mScreen = -1;
    /* access modifiers changed from: private */
    public String mVideoId;
    private String mVideoTemplate;
    /* access modifiers changed from: private */
    public WebView mWebView;
    /* access modifiers changed from: private */
    public WebView mWebViewPlay;
    /* access modifiers changed from: private */
    public int mWidth;
    int mWidthIncr = 0;
    int rotation = 0;
    private static final String TAG = "MainActivity";
    Runnable runDisplayStatus = new Runnable() {
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (MainActivity.this.mDuration <= 0) {
                        return;
                    }
                    if (MainActivity.this.mScreen == 3 || MainActivity.this.mScreen == 1) {
                        MainActivity.this.txtTime.setText(MainActivity.this.formatTimeShort((long) MainActivity.this.mCurrTime));
                        MainActivity.this.txtDuration.setText(MainActivity.this.formatTimeShort((long) MainActivity.this.mDuration));
                        MainActivity.this.seekVideo.setProgress(MainActivity.this.mCurrPerc);
                        MainActivity.this.showViews(R.id.layoutSeek, R.id.layoutZoom, R.id.gridActions);
                        if (MainActivity.this.mVideoId.length() > 11) {
                            MainActivity.this.showViews(R.id.imgNext, R.id.imgPrev);
                        } else {
                            MainActivity.this.hideViews(R.id.imgNext, R.id.imgPrev);
                        }
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public SeekBar seekVideo;
    private SeekBar seekZoom;
    SharedPreferences sharedPreferences;
    Handler syncHandler = new Handler();
    long syncingTime = 100;
    TextureView textureView = null;
    /* access modifiers changed from: private */
    public TextView txtDuration;
    /* access modifiers changed from: private */
    public TextView txtTime;
    /* access modifiers changed from: private */
    public TextView txtZoom;
    int uiOptions;
    private boolean user_has_played_video;

    public void doNothing(View view) {
    }

    public void onBackPressed() {
    }

    public void progressClicked(View view) {
    }

    /* access modifiers changed from: package-private */
    public void showUI() {
    }

    @SuppressLint({"SourceLockedOrientationActivity", "JavascriptInterface"})
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().addFlags(128);
        getWindow().addFlags(1024);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setVolumeControlStream(3);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(bundle);
        try {
            XWalkPreferences.setValue("animatable-xwalk-view", true);
        } catch (Exception unused) {
        }
        if (!this.sharedPreferences.contains("QUALITY")) {
            setPref("QUALITY", this.sharedPreferences.getBoolean("HQ", false) ? "MQ" : "LQ");
        }
        this.mQuality = this.sharedPreferences.getString("QUALITY", "LQ");
        setContentView(R.layout.activity_main);
        this.dividerLine = findViewById(R.id.dividerLine);
//        MobileAds.initialize(this.context, "ca-app-pub-3449269969535138~4009633608");
        this.isPRO = this.sharedPreferences.getBoolean("IS_PRO", false);
        this.user_has_played_video = this.sharedPreferences.getBoolean("USER_HAS_PLAYED_VIDEO", false);
        if (this.user_has_played_video) {
            inviViews(R.id.imgHelp, R.id.imgLogo);
        }
        this.layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
        this.layoutWebView = (RelativeLayout) findViewById(R.id.layoutWebView);
        this.layoutAdView = (LinearLayout) findViewById(R.id.layoutAdView);
        this.mWebView = (WebView) findViewById(R.id.webView);
        this.mWebViewPlay = (WebView) findViewById(R.id.webViewPlay);
        this.imgDoubler = (FrameDoublerView) findViewById(R.id.imgDoubler);
        this.layoutAd = (LinearLayout) findViewById(R.id.layoutAd);
        this.clickGuard = findViewById(R.id.clickGuard);
        this.layoutProgress = (LinearLayout) findViewById(R.id.layoutProgress);
        this.txtTime = (TextView) findViewById(R.id.txtTime);
        this.txtDuration = (TextView) findViewById(R.id.txtDuration);
        this.seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        this.txtZoom = (TextView) findViewById(R.id.txtZoom);
        this.seekZoom = (SeekBar) findViewById(R.id.seekZoom);
        this.btnPickVideo = (ImageView) findViewById(R.id.btnPickVideo);
        this.gridActions = (GridView) findViewById(R.id.gridActions);
        this.imgBack = (ImageView) findViewById(R.id.imgBack);
        /*this.mBillingClient = BillingClient.newBuilder(this.context).setListener(this).build();
        this.mBillingClient.startConnection(new BillingClientStateListener() {
            public void onBillingServiceDisconnected() {
            }

            public void onBillingSetupFinished(int i) {
                if (i == 0) {
                    MainActivity.this.validatePurchase();
                }
            }
        });*/
        try {
            ((TextView) findViewById(R.id.txtVersion)).setText("Ver. " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception unused2) {
        }
        hideUI();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int i) {
                if ((i & 4) == 0) {
                    MainActivity.this.hideUI();
                }
            }
        });
        updateScreenSizesExt();
        resizeViews(this.W / 2, 0, 0, this.mWebView);
        resizeViews(this.W / 2, 0, 0, R.id.layoutAd, R.id.layoutSeek, R.id.layoutZoom, R.id.layoutHelp, R.id.layoutToolbar, R.id.gridActions, R.id.imgError);
        resizeViews(this.W / 10, 0, 0, this.txtDuration, this.txtTime);
        resizeViews((this.W * 3) / 10, 0, 0, this.seekVideo);
        this.seekVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                WebView access$100 = MainActivity.this.mWebViewPlay;
                access$100.loadUrl("javascript: seekVideo(" + seekBar.getProgress() + ");");
                MainActivity.this.playVideo();
            }
        });
        this.seekZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                int i2 = (i * 5) + 50;
                MainActivity.this.setPref("ZOOM", i2);
                TextView access$300 = MainActivity.this.txtZoom;
                access$300.setText("" + i2 + "%");
                MainActivity.this.imgDoubler.setImageSize(MainActivity.this.W, MainActivity.this.H, MainActivity.this.mWidth, MainActivity.this.mHeight, MainActivity.this.isFullScr, i2, MainActivity.this.rotation);
            }
        });
        this.gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent motionEvent) {
                if (!MainActivity.this.processLink(false)) {
                    super.onLongPress(motionEvent);
                }
            }

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return MainActivity.this.processLink(true);
            }
        });
        this.mWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MainActivity.this.hideUI();
                return MainActivity.this.gestureDetector.onTouchEvent(motionEvent);
            }
        });
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        WebSettings settings2 = this.mWebViewPlay.getSettings();
        settings2.setJavaScriptEnabled(true);
        settings2.setLoadWithOverviewMode(false);
        settings2.setUseWideViewPort(false);
        this.mWebView.setWebViewClient(new MyXWalkResourceClient(this.mWebView));
        this.mWebViewPlay.addJavascriptInterface(new IJavascriptHandler(), "AndroidApp");
        this.mVideoTemplate = getTemplate("template.txt");
        if (!processIntent()) {
            showPlayer(false);
        } else {
            this.isIntent = true;
        }
        this.gestureDetectorMain = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent motionEvent) {
                if (MainActivity.this.mScreen == 0 && !MainActivity.this.processLink(false)) {
                    super.onLongPress(motionEvent);
                }
            }

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (MainActivity.this.mScreen != 0 || MainActivity.this.processLink(true)) {
                    return true;
                }
                return false;
            }
        });
        this.mWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return MainActivity.this.gestureDetectorMain.onTouchEvent(motionEvent);
            }
        });
        this.gestureDetectorPlay = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_MAX_OFF_PATH = 200;
            private static final int SWIPE_MIN_DISTANCE = 100;
            private static final int SWIPE_THRESHOLD_VELOCITY = 50;
            private MotionEvent mLastOnDownEvent = null;

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (MainActivity.this.mScreen == -1) {
                    MainActivity.this.btnPickVideoClicked((View) null);
                } else if (MainActivity.this.mScreen == 1) {
                    MainActivity.this.playVideo();
                } else if (MainActivity.this.mScreen == 2) {
                    MainActivity.this.pauseVideo();
                } else {
                    MainActivity.this.playVideo();
                }
                return true;
            }

            public boolean onDown(MotionEvent motionEvent) {
                this.mLastOnDownEvent = motionEvent;
                return super.onDown(motionEvent);
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (MainActivity.this.mScreen == -1 || MainActivity.this.mScreen == 0) {
                    return false;
                }
                if (motionEvent == null) {
                    motionEvent = this.mLastOnDownEvent;
                }
                if (motionEvent == null || motionEvent2 == null) {
                    return false;
                }
                try {
                    float x = motionEvent2.getX() - motionEvent.getX();
                    if (Math.abs(motionEvent.getY() - motionEvent2.getY()) >= 200.0f || Math.abs(f) < 50.0f || Math.abs(x) < 100.0f) {
                        return false;
                    }
                    if (x > 0.0f) {
                        MainActivity.this.mWebViewPlay.loadUrl("javascript:seekBackward();");
                        MainActivity.this.say((CharSequence) "<< -10%");
                        MainActivity.this.playVideo();
                        return true;
                    }
                    MainActivity.this.mWebViewPlay.loadUrl("javascript:seekForward();");
                    MainActivity.this.say((CharSequence) "+10% >>");
                    MainActivity.this.playVideo();
                    return true;
                } catch (Exception unused) {
                    return false;
                }
            }
        });
        this.clickGuard.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return MainActivity.this.gestureDetectorPlay.onTouchEvent(motionEvent);
            }
        });
        this.mWebView.loadUrl("http://m.youtube.com");
        if (this.user_has_played_video) {
            btnPickVideoClicked((View) null);
        } else {
            blinkView(this.btnPickVideo);
        }
    }

    public void imgCloseAdClicked(View view) {
//        this.mBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSku(SKU_ID).setType(BillingClient.SkuType.INAPP).build());
    }

    public void testCloseAdClicked(View view) {
        this.isPRO = true;
        hideBannerAd();
    }

    /* access modifiers changed from: private */
    public void validatePurchase() {
        /*List<Purchase> purchasesList = this.mBillingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
        this.isPRO = false;
        if (purchasesList != null && !purchasesList.isEmpty()) {
            for (Purchase sku : purchasesList) {
                if (sku.getSku().equals(SKU_ID)) {
                    this.isPRO = true;
                }
            }
        }
        setPref("IS_PRO", this.isPRO);
        if (this.isPRO) {
            hideBannerAd();
        }*/
    }

    /*public void onPurchasesUpdated(int i, @Nullable List<Purchase> list) {
        if (i == 0 && list != null) {
            validatePurchase();
        }
    }*/

    /* access modifiers changed from: package-private */
    public void initBannerAd() {
        /*this.mAdView = new AdView(this.context);
        this.mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        this.mAdView.setAdUnitId("ca-app-pub-3449269969535138/3794687205");
        this.layoutAdView.addView(this.mAdView);
        this.mAdView.loadAd(new AdRequest.Builder().build());*/
    }

    /* access modifiers changed from: package-private */
    public void showBannerAd() {
        /*if (this.isPRO) {
            hideBannerAd();
            return;
        }
        showViews(this.layoutAd, this.layoutAdView);
        showViews(R.id.imgCloseAd);
        if (this.mAdView == null) {
            initBannerAd();
        }
        this.mAdView.setEnabled(true);
        this.mAdView.setVisibility(View.VISIBLE)*/;
    }

    /* access modifiers changed from: package-private */
    public void hideBannerAd() {
        /*if (this.mAdView != null) {
            this.layoutAdView.removeView(this.mAdView);
            this.mAdView.destroy();
            this.mAdView = null;
        }
        hideViews(this.layoutAd, this.layoutAdView);
        if (this.mScreen != 2) {
            showViews(this.layoutAd, this.layoutAdView);
        }
        hideViews(R.id.imgCloseAd);*/
    }

    public void btnHelpClicked(View view) {
        setPref("USER_HAS_PLAYED_VIDEO", false);
        recreate();
    }

    private void updateResume() {
        if (isVideoLoaded()) {
            showViews(R.id.btnResume);
            hideViews(R.id.btnHelp);
            return;
        }
        hideViews(R.id.btnResume);
    }

    private void blinkView(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.3f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(-1);
        alphaAnimation.setRepeatMode(2);
        view.clearAnimation();
        view.startAnimation(alphaAnimation);
    }

    public class MyXWalkResourceClient extends WebViewClient {
        public MyXWalkResourceClient(WebView xWalkView) {
            super();
        }

        public void onReceivedLoadError(XWalkView xWalkView, int i, String str, String str2) {
            if (MainActivity.this.isVisible(R.id.webView)) {
                MainActivity.this.showViews(R.id.imgError);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        MainActivity.this.hideViews(R.id.imgError);
                    }
                }, 2000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideToolbar(int i) {
        this.hideToolbarHandler.removeCallbacksAndMessages((Object) null);
        this.hideToolbarHandler.postDelayed(new Runnable() {
            public void run() {
                MainActivity.this.hideViews(R.id.layoutToolbar, R.id.layoutSeek, R.id.layoutZoom, R.id.gridActions, R.id.layoutAd, R.id.imgNext, R.id.imgPrev);
            }
        }, (long) i);
    }

    /* access modifiers changed from: private */
    public void updateSeekBar() {
        runOnUiThread(this.runDisplayStatus);
    }

    final class IJavascriptHandler {
        IJavascriptHandler() {
        }

        @JavascriptInterface
        public void onLoad(int i) {
            int unused = MainActivity.this.mScreen = 1;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.hideViews(MainActivity.this.layoutProgress);
                }
            });
            MainActivity.this.mDuration = i;
            int i2 = 0;
            MainActivity.this.mCurrTime = 0;
            MainActivity mainActivity = MainActivity.this;
            if (MainActivity.this.mDuration > 0) {
                i2 = (MainActivity.this.mCurrTime * 100) / MainActivity.this.mDuration;
            }
            int unused2 = mainActivity.mCurrPerc = i2;
            MainActivity.this.updateSeekBar();
        }

        @JavascriptInterface
        public void onPlay() {
            int unused = MainActivity.this.mScreen = 2;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.hideToolbar(1000);
                }
            });
        }

        @JavascriptInterface
        public void onVideoEnd() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.pauseVideo();
                }
            });
        }

        @JavascriptInterface
        public void getSeekData(int i, int i2) {
            MainActivity.this.mDuration = i;
            MainActivity.this.mCurrTime = i2;
            int unused = MainActivity.this.mCurrPerc = MainActivity.this.mDuration > 0 ? (MainActivity.this.mCurrTime * 100) / MainActivity.this.mDuration : 0;
            MainActivity.this.updateSeekBar();
        }
    }

    /* access modifiers changed from: package-private */
    public String pad2(int i) {
        StringBuilder sb;
        String str;
        if (i > 9) {
            sb = new StringBuilder();
            str = "";
        } else {
            sb = new StringBuilder();
            str = "0";
        }
        sb.append(str);
        sb.append(i);
        return sb.toString();
    }

    public String formatTimeShort(long j) {
        return ((int) ((j / 3600) % 24)) + ":" + pad2((int) ((j / 60) % 60)) + ":" + pad2((int) (j % 60));
    }

    /* access modifiers changed from: package-private */
    public boolean processLink(boolean z) {
        if (!openVideo(this.mWebView.getHitTestResult().getExtra(), z) && !openVideo(this.mWebView.getUrl(), z)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void showPlayer(boolean z) {
        showViews(R.id.layoutAd);
        this.syncHandler.removeCallbacksAndMessages((Object) null);
        this.mScreen = -1;
        hideUI();
        showViews(R.id.dividerLine);
        updateGridActions();
        this.mWidth = !this.mQuality.equals("LQ") ? this.W / 2 : this.isTall ? this.W / 3 : this.W / 4;
        if (this.mWidth < 200) {
            this.mWidth = 200;
        }
        this.mHeight = Math.min(this.H, this.isTall ? (this.mWidth * 3) / 4 : (this.mWidth * 10) / 16);
        if (this.mHeight > this.H) {
            this.mHeight = this.H;
        }
        this.imgDoubler.setImageSize(this.W, this.H, this.mWidth, this.mHeight, this.isFullScr, getZoom(), this.rotation);
        resizeViews(this.mWidth, this.mHeight, 0, this.mWebViewPlay);
        showViews(this.clickGuard);
        if (z) {
            hideViews(R.id.layoutToolbar);
        } else {
            showViews(R.id.layoutToolbar);
        }
        if (this.mVideoId == null || this.mVideoId.isEmpty()) {
            hideViews(this.mWebView, this.mWebViewPlay);
            this.imgDoubler.setBitmap(this.defaultBitmap);
            hideViews(this.imgDoubler);
            showViews(R.id.layoutToolbar);
            showViews(R.id.layoutHelp);
            return;
        }
        updateResume();
        updateNav();
        hideViews(R.id.layoutHelp, R.id.layoutHelpLeft);
        this.imgDoubler.bringToFront();
        hideViews(this.mWebView);
        showViews(this.mWebViewPlay);
        showViews(this.imgDoubler);
        if (!z) {
            this.mCurrPerc = 0;
        }
        if (this.mCurrPerc == 0) {
            showViews(R.id.layoutToolbar);
        }
        this.mWebView.stopLoading();
//        WebVN navigationHistory = this.mWebView.getNavigationHistory();
        if (canGoBack()) {
            mWebView.goBack();
        }
        loadVideo(this.mVideoId, this.mWidth, this.mHeight);
        syncDualView();
        this.mDuration = 0;
        this.mCurrTime = 0;
        updateSeekBar();
        int zoom = getZoom();
        this.seekZoom.setProgress((zoom - 50) / 5);
        TextView textView = this.txtZoom;
        textView.setText("" + zoom + "%");
        showLeftBars();
    }

    private void showLeftBars() {
        hideBannerAd();
        showViews(this.layoutAd);
        inviViews(this.layoutAdView);
    }

    private void updateNav() {
        if (this.mScreen != 0) {
            this.imgBack.setAlpha(1.0f);
        } else if (canGoBack()) {
            this.imgBack.setAlpha(1.0f);
        } else {
            this.imgBack.setAlpha(0.5f);
        }
    }

    /* access modifiers changed from: private */
    public int getZoom() {
        return this.sharedPreferences.getInt("ZOOM", 100);
    }

    public void imgZoomClicked(View view) {
        setPref("ZOOM", 100);
        this.txtZoom.setText("100%");
        this.seekZoom.setProgress(10);
        this.imgDoubler.setImageSize(this.W, this.H, this.mWidth, this.mHeight, this.isFullScr, 100, this.rotation);
    }

    /* access modifiers changed from: private */
    public void updateGridActions() {
        Log.e(TAG, "updateGridActions: " );
        this.isFullScr = this.sharedPreferences.getBoolean("FULLSCR", false);
        this.isTall = this.sharedPreferences.getBoolean("TALL", false);
        this.isLoop = this.sharedPreferences.getBoolean("LOOP", true);
        Integer[] numArr = new Integer[5];
        numArr[0] = this.isFullScr ? R.drawable.ic_fullscreen : R.drawable.ic_fullscreen_off;
        numArr[1] = this.mQuality.equals("LQ") ? R.drawable.ic_lq : this.mQuality.equals("HQ") ? R.drawable.ic_hq : R.drawable.ic_mq;
        numArr[2] = this.isTall ? R.drawable.ic_tall : R.drawable.ic_wide;
        numArr[3] = this.isLoop ? R.drawable.ic_repeat : R.drawable.ic_repeat_off;
        numArr[4] = R.drawable.ic_rotate;
        this.gridActions.setNumColumns(numArr.length);
        this.gridActions.setAdapter(new ToolbarAdapter(this.context, numArr));
        this.gridActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                switch (i) {
                    case 0:
                        boolean unused = MainActivity.this.isFullScr = !MainActivity.this.isFullScr;
                        MainActivity.this.setPref("FULLSCR", MainActivity.this.isFullScr);
                        MainActivity.this.imgDoubler.setImageSize(MainActivity.this.W, MainActivity.this.H, MainActivity.this.mWidth, MainActivity.this.mHeight, MainActivity.this.isFullScr, MainActivity.this.getZoom(), MainActivity.this.rotation);
                        break;
                    case 1:
                        if (MainActivity.this.mQuality.equals("LQ")) {
                            String unused2 = MainActivity.this.mQuality = "MQ";
                        } else if (MainActivity.this.mQuality.equals("MQ")) {
                            String unused3 = MainActivity.this.mQuality = "HQ";
                        } else {
                            String unused4 = MainActivity.this.mQuality = "LQ";
                        }
                        MainActivity.this.setPref("QUALITY", MainActivity.this.mQuality);
                        if (MainActivity.this.mScreen == 3) {
                            MainActivity.this.hideToolbar(100);
                        }
                        MainActivity.this.showPlayer(true);
                        break;
                    case 2:
                        boolean unused5 = MainActivity.this.isTall = !MainActivity.this.isTall;
                        MainActivity.this.setPref("TALL", MainActivity.this.isTall);
                        if (MainActivity.this.mScreen == 3) {
                            MainActivity.this.hideToolbar(100);
                        }
                        MainActivity.this.showPlayer(true);
                        break;
                    case 3:
                        boolean unused6 = MainActivity.this.isLoop = !MainActivity.this.isLoop;
                        MainActivity.this.setPref("LOOP", MainActivity.this.isLoop);
                        WebView access$2700 = MainActivity.this.mWebView;
                        StringBuilder sb = new StringBuilder();
                        sb.append("javascript:setLoop(");
                        sb.append(MainActivity.this.isLoop ? "true" : "false");
                        sb.append(");");
                        access$2700.loadUrl(sb.toString());
                        break;
                    case 4:
                        MainActivity mainActivity = MainActivity.this;
                        int i2 = 180;
                        if (MainActivity.this.rotation == 0) {
                            MainActivity.this.rotation = 90;
                            i2 = 90;
                        } else if (MainActivity.this.rotation != 90) {
                            i2 = MainActivity.this.rotation == 180 ? 270 : 0;
                        }
                        mainActivity.rotation = i2;
                        MainActivity.this.imgDoubler.setImageSize(MainActivity.this.W, MainActivity.this.H, MainActivity.this.mWidth, MainActivity.this.mHeight, MainActivity.this.isFullScr, MainActivity.this.getZoom(), MainActivity.this.rotation);
                        break;
                }
                MainActivity.this.updateGridActions();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public boolean isVideoLoaded() {
        return this.mVideoId != null && !this.mVideoId.isEmpty();
    }

    public void btnResumeClicked(View view) {
        if (!isVideoLoaded()) {
            return;
        }
        if (this.mScreen == 0) {
            if (this.mScreen == 3) {
                hideToolbar(100);
            }
            showPlayer(true);
            return;
        }
        playVideo();
    }

    /* access modifiers changed from: package-private */
    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void imgRateClicked(View view) {
        openLink((int) R.string.rate_url);
    }

    public void imgShareClicked(View view) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_url));
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public void openLink(int i) {
        openLink(getString(i));
    }

    /* access modifiers changed from: package-private */
    public void openLink(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public void say(int i) {
        Toast.makeText(this, i, Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: package-private */
    public void say(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: private */
    public void showList() {
        showViews(R.id.layoutAd);
        this.mScreen = 0;
        updateResume();
        hideViews(R.id.layoutHelp);
        hideViews(this.mWebViewPlay);
        this.syncHandler.removeCallbacksAndMessages((Object) null);
        hideViews(this.layoutProgress);
        showUI();
        this.mWebView.bringToFront();
        showBannerAd();
        showViews(this.mWebView);
        showViews(this.imgDoubler);
        hideViews(this.clickGuard);
        hideViews(R.id.layoutSeek, R.id.layoutZoom, R.id.gridActions, R.id.imgPrev, R.id.imgNext);
        showViews(R.id.layoutToolbar);
    }

    /* access modifiers changed from: private */
    public void playVideo() {
        this.mScreen = 2;
        this.mWebViewPlay.loadUrl("javascript:playVideos()");
        hideToolbar(1000);
        hideViews(R.id.layoutAd);
        hideBannerAd();
        if (!this.user_has_played_video) {
            this.user_has_played_video = true;
            setPref("USER_HAS_PLAYED_VIDEO", true);
        }
    }

    /* access modifiers changed from: private */
    public void pauseVideo() {
        this.mScreen = 3;
        this.mWebViewPlay.loadUrl("javascript:pauseVideos()");
        showViews(R.id.layoutToolbar, R.id.gridActions, R.id.layoutSeek);
        if (this.mVideoId == null || this.mVideoId.length() <= 11) {
            hideViews(R.id.imgNext, R.id.imgPrev);
        } else {
            showViews(R.id.imgNext, R.id.imgPrev);
        }
        showBannerAd();
    }

    /* access modifiers changed from: package-private */
    public String getTemplate(String str) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(str), "UTF-8"));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine + "\n");
                } else {
                    bufferedReader.close();
                    return sb.toString();
                }
            }
        } catch (Exception unused) {
            return "";
        }
    }

    /* access modifiers changed from: package-private */
    public String getVideoHTML(String str, int i, int i2) {
        String str2 = this.mVideoTemplate;
        String replace = str2.replace("<W>", "" + i);
        String replace2 = replace.replace("<H>", "" + i2).replace("<VIDEOID>", str).replace("<QUALITY>", this.mQuality.equals("HQ") ? "hires" : "small");
        return replace2.replace("<STARTPERC>", "" + this.mCurrPerc).replace("<LOOP>", this.isLoop ? "true" : "false");
    }

    /* access modifiers changed from: package-private */
    public void syncDualView() {
        if (!this.isPaused) {
            synchronized (this) {
                long currentTimeMillis = System.currentTimeMillis();
                this.imgDoubler.setBitmap(captureImage());
                this.syncingTime = System.currentTimeMillis() - currentTimeMillis;
            }
        } else {
            this.syncingTime = 100;
        }
        this.syncHandler.postDelayed(new Runnable() {
            public void run() {
                MainActivity.this.syncDualView();
            }
        }, this.syncingTime);
    }

    public Bitmap captureImage() {
        if (this.mWebViewPlay == null) {
            return null;
        }
        try {
            if (this.textureView == null) {
                this.textureView = findXWalkTextureView(this.mWebViewPlay);
            }
            if (this.textureView != null) {
                return this.textureView.getBitmap();
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    private TextureView findXWalkTextureView(ViewGroup viewGroup) {
        TextureView findXWalkTextureView;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof TextureView) {
                if (childAt.getParent().getClass().toString().contains("XWalk")) {
                    return (TextureView) childAt;
                }
            } else if ((childAt instanceof ViewGroup) && (findXWalkTextureView = findXWalkTextureView((ViewGroup) childAt)) != null) {
                return findXWalkTextureView;
            }
        }
        return null;
    }

    public void exitClicked(View view) {
        if (this.isPRO) {
            finish();
            System.exit(0);
            return;
        }
        View inflate = View.inflate(this.context, R.layout.exit_dialog, (ViewGroup) null);
        final AlertDialog show = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert)).setView(inflate).show();
        inflate.findViewById(R.id.imgExitLogo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                show.dismiss();
                MainActivity.this.moreClicked((View) null);
                MainActivity.this.finish();
                System.exit(0);
            }
        });
        inflate.findViewById(R.id.imgExitCancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                show.dismiss();
            }
        });
        inflate.findViewById(R.id.imgExit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                show.dismiss();
                MainActivity.this.finish();
                System.exit(0);
            }
        });
    }

    public void moreClicked(View view) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.more_url))));
        } catch (Exception unused) {
        }
    }

    private class ImageGetter implements Html.ImageGetter {
        private ImageGetter() {
        }

        public Drawable getDrawable(String str) {
            Drawable drawable;
            if (str.startsWith("#")) {
                drawable = new BitmapDrawable(MainActivity.this.getResources(), str.substring(1));
            } else {
                drawable = MainActivity.this.getResources().getDrawable(MainActivity.this.getResources().getIdentifier(str, "drawable", MainActivity.this.getPackageName()));
            }
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        }
    }

    public void imgYoutubeClicked(View view) {
        AlertDialog.Builder title = new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.powered_by_youtube);
        ((TextView) title.setMessage(Html.fromHtml(String.format(getString(R.string.youtube_text), new Object[]{getString(R.string.app_name)})
                + "<br><br><a href='http://www.youtube.com'>YouTube</a><br><a href='https://developers.google.com/youtube/terms'>YouTube API Services Terms of Service</a>"))
                .setPositiveButton("ok", (DialogInterface.OnClickListener) null)
                .show()
                .findViewById(16908299))
                .setMovementMethod(new LinkMovementMethod());
    }

    public void imgBackClicked(View view) {
        backPressed();
    }

    public void imgDoublerClicked(View view) {
        btnResumeClicked((View) null);
    }

    public void btnPickVideoClicked(View view) {
        this.btnPickVideo.clearAnimation();
        showViews(this.mWebView);
        hideViews(R.id.layoutHelp, R.id.layoutHelpLeft);
        showViews(R.id.layoutAd);
        this.btnPickVideo.removeCallbacks((Runnable) null);
        this.btnPickVideo.postDelayed(new Runnable() {
            public void run() {
                boolean z;
//                XWalkNavigationHistory navigationHistory = MainActivity.this.mWebView.getNavigationHistory();
                try {
                    z = mWebView.getTitle().isEmpty();
                } catch (Exception unused) {
                    z = false;
                }
                if (MainActivity.this.mScreen == 0) {
                    if (!mWebView.canGoBack()) {
                        MainActivity.this.mWebView.loadUrl("http://m.youtube.com");
                    } else if (!z) {
                        MainActivity.this.mWebView.reload();
                    }
                }
                if (z) {
                    if (MainActivity.this.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        MainActivity.this.mWebView.loadUrl("http://m.youtube.com");
                    }
                }
                MainActivity.this.showList();
            }
        }, 200);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 4) {
            backPressed();
            return true;
        }
        if (keyCode == 25 || keyCode == 24 || keyCode == 164) {
            hideUI();
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    private void backPressed() {
        if (System.currentTimeMillis() - this.last_back_press_time >= 500) {
            this.last_back_press_time = System.currentTimeMillis();
            if (this.mScreen == 0) {
                updateNav();
                if (canGoBack()) {
                    this.mWebView.goBack();
                }
            } else if (this.mScreen != 2 || this.mVideoId == null) {
                btnPickVideoClicked((View) null);
            } else {
                pauseVideo();
            }
        }
    }

    private boolean processIntent() {
        Uri data;
        try {
            Intent intent = getIntent();
            String type = intent.getType();
            String action = intent.getAction();
            if (!"android.intent.action.SEND".equals(action) || type == null || !type.startsWith("text/")) {
                if ("android.intent.action.VIEW".equals(action) && (data = intent.getData()) != null) {
                    openVideo(data.toString(), false);
                    return true;
                }
                return false;
            }
            String stringExtra = intent.getStringExtra("android.intent.extra.TEXT");
            if (stringExtra != null) {
                openVideo(stringExtra.trim(), false);
                return true;
            }
            return false;
        } catch (Exception unused) {
        }
        return false;
    }

    public void prevClicked(View view) {
        this.mWebViewPlay.loadUrl("javascript:prevVideo()");
    }

    public void nextClicked(View view) {
        this.mWebViewPlay.loadUrl("javascript:nextVideo()");
    }

    /* access modifiers changed from: package-private */
    public boolean openVideo(String str, boolean z) {
        if (str != null) {
            try {
                if (!str.isEmpty()) {
                    String videoIdCoarse = getVideoIdCoarse(str);
                    if (!videoIdCoarse.isEmpty()) {
                        this.mWebView.loadUrl("about:blank");
                        showViews(this.layoutProgress);
                        this.layoutProgress.postDelayed(new Runnable() {
                            public void run() {
                                MainActivity.this.hideViews(MainActivity.this.layoutProgress);
                            }
                        }, 5000);
                        this.mVideoId = videoIdCoarse;
                        this.rotation = 0;
                        showPlayer(false);
                        return true;
                    }
                }
            } catch (Exception unused) {
            }
        }
        return false;
    }

    private void loadVideo(String str, int i, int i2) {
        this.mWebViewPlay.loadUrl("http://www.panagola.com");
        showViews(R.id.layoutToolbar);
        this.mScreen = 1;
    }

    /* access modifiers changed from: package-private */
    public String getVideoIdCoarse(String str) {
        try {
            String url = this.mWebView.getUrl();
            if (url == null || url.contains("/channel")) {
                return "";
            }
            if (url.contains("/user")) {
                return "";
            }
            String playList = getPlayList(url);
            if (playList != null) {
                return playList;
            }
            String[] strArr = {"^.*[\\/=]([\\w\\-]{11})$", "^.*[\\/=]([\\w\\-]{11})[\\/&?#].*$"};
            for (String compile : strArr) {
                Matcher matcher = Pattern.compile(compile, Pattern.CASE_INSENSITIVE).matcher(str);
                if (matcher.matches() && !matcher.group(1).equalsIgnoreCase("select_site")) {
                    return matcher.group(1);
                }
            }
            return "";
        } catch (Exception unused) {
            return "";
        }
    }

    private String getPlayList(String str) {
        try {
            Matcher matcher = Pattern.compile("^.*[&?]list=([a-zA-Z0-9_]+).*$", Pattern.CASE_INSENSITIVE).matcher(str);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mWebView != null) {
            this.mWebView.pauseTimers();
            this.mWebView.setVisibility(View.GONE);
        }
        if (this.mWebViewPlay != null) {
            this.mWebViewPlay.pauseTimers();
            this.mWebViewPlay.setVisibility(View.GONE);
        }
        this.isPaused = true;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        hideUI();
        if (this.mWebView != null) {
            this.mWebView.resumeTimers();
            this.mWebView.setVisibility(View.VISIBLE);
        }
        if (this.mWebViewPlay != null) {
            this.mWebViewPlay.resumeTimers();
            this.mWebViewPlay.setVisibility(View.VISIBLE);
//            this.mWebViewPlay.onShow();
        }
        this.isPaused = false;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.syncHandler.removeCallbacksAndMessages((Object) null);
        super.onDestroy();
        if (this.mWebView != null) {
//            this.mWebView.onDestroy();
        }
        if (this.mWebViewPlay != null) {
//            this.mWebViewPlay.onDestroy();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (this.mWebView != null) {
//            this.mWebView.onActivityResult(i, i2, intent);
        }
        if (this.mWebViewPlay != null) {
//            this.mWebViewPlay.onActivityResult(i, i2, intent);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        if (this.mWebView != null) {
//            this.mWebView.onNewIntent(intent);
        }
        if (this.mWebViewPlay != null) {
//            this.mWebViewPlay.onNewIntent(intent);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isVisible(int i) {
        return findViewById(i).getVisibility() == View.VISIBLE;
    }

    /* access modifiers changed from: package-private */
    public void hideViews(int... iArr) {
        for (int findViewById : iArr) {
            findViewById(findViewById).setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideViews(View... viewArr) {
        for (View visibility : viewArr) {
            visibility.setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: package-private */
    public void inviViews(View... viewArr) {
        for (View visibility : viewArr) {
            visibility.setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: package-private */
    public void inviViews(int... iArr) {
        for (int findViewById : iArr) {
            findViewById(findViewById).setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: package-private */
    public void showViews(int... iArr) {
        for (int findViewById : iArr) {
            findViewById(findViewById).setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: package-private */
    public void showViews(View... viewArr) {
        for (View visibility : viewArr) {
            visibility.setVisibility(View.GONE);
        }
    }

    /* access modifiers changed from: package-private */
    public void setPref(String str, boolean z) {
        this.sharedPreferences.edit().putBoolean(str, z).commit();
    }

    /* access modifiers changed from: package-private */
    public void setPref(String str, String str2) {
        this.sharedPreferences.edit().putString(str, str2).commit();
    }

    /* access modifiers changed from: package-private */
    public void setPref(String str, int i) {
        this.sharedPreferences.edit().putInt(str, i).commit();
    }

    /* access modifiers changed from: package-private */
    public void resizeViews(int i, int i2, int i3, View... viewArr) {
        for (View view : viewArr) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (i > 0) {
                layoutParams.width = i;
            }
            if (i2 > 0) {
                layoutParams.height = i2;
            }
            view.setLayoutParams(layoutParams);
            if (i3 > 0) {
                setTextSize(view, i3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void resizeViews(int i, int i2, int i3, int... iArr) {
        for (int i4 : iArr) {
            View findViewById = findViewById(i4);
            ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
            if (i > 0) {
                layoutParams.width = i;
            }
            if (i2 > 0) {
                layoutParams.height = i2;
            }
            findViewById.setLayoutParams(layoutParams);
            if (i3 > 0) {
                setTextSize(i4, i3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setTextSize(int i, int i2) {
        setTextSize(findViewById(i), i2);
    }

    /* access modifiers changed from: package-private */
    public void setTextSize(View view, int i) {
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(0, (float) i);
        } else if (view instanceof Button) {
            ((Button) view).setTextSize(0, (float) i);
        } else if (view instanceof EditText) {
            ((EditText) view).setTextSize(0, (float) i);
        } else if (view instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) view).setTextSize(0, (float) i);
        } else if (view instanceof CheckBox) {
            ((CheckBox) view).setTextSize(0, (float) i);
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 25 || i == 24) {
            hideUI();
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* access modifiers changed from: package-private */
    public void hideUI() {
        if (Build.VERSION.SDK_INT >= 19) {
            this.uiOptions = 5894;
            getWindow().getDecorView().setSystemUiVisibility(this.uiOptions);
            this.mHideUiHandler.removeCallbacksAndMessages((Object) null);
            this.mHideUiHandler.postDelayed(new Runnable() {
                public void run() {
                    MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.uiOptions);
                }
            }, 500);
        }
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        hideUI();
    }

    /* access modifiers changed from: package-private */
    public int dpToPx(int i) {
        return Math.round(((float) i) * (getResources().getDisplayMetrics().xdpi / 160.0f));
    }

    public void imgScrZoomInClicked(View view) {
        int scrZoomStep = getScrZoomStep();
        if (scrZoomStep > 0) {
            setPref("SCR_ZOOM_STEP", scrZoomStep - 1);
            recreate();
        }
    }

    public void imgScrZoomOutClicked(View view) {
        int scrZoomStep = getScrZoomStep();
        if (scrZoomStep < this.STEPS) {
            setPref("SCR_ZOOM_STEP", scrZoomStep + 1);
            recreate();
        }
    }

    private int getScrZoomStep() {
        return this.sharedPreferences.getInt("SCR_ZOOM_STEP", 0);
    }

    /* access modifiers changed from: package-private */
    public void updateScreenSizesExt() {
        int[] screenSize = getScreenSize();
        this.W = screenSize[0];
        this.H = screenSize[1];
        this.mMaxWidth = this.W;
        this.mMinWidth = (this.H * 16) / 9;
        if (this.W - this.mMinWidth < 20) {
            hideViews(R.id.imgScrZoomIn, R.id.imgScrZoomOut);
        } else {
            this.mWidthIncr = (this.W - this.mMinWidth) / this.STEPS;
            int scrZoomStep = getScrZoomStep();
            if (scrZoomStep == 0) {
                hideViews(R.id.imgScrZoomIn);
            } else if (scrZoomStep == this.STEPS - 1) {
                hideViews(R.id.imgScrZoomOut);
            }
            this.W -= this.mWidthIncr * scrZoomStep;
            resizeViews(this.W, 0, 0, this.layoutMain);
        }
        this.MIN_W = Math.min(this.W, this.H);
        this.MAX_W = Math.max(this.W, this.H);
    }

    public int[] getScreenSize() {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        if (Build.VERSION.SDK_INT < 19) {
            return new int[]{i, i2};
        }
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                int intValue = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
                try {
                    i2 = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(defaultDisplay, new Object[0])).intValue();
                } catch (Exception unused) {
                }
                i = intValue;
            } catch (Exception unused2) {
            }
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point point = new Point();
                Display.class.getMethod("getRealSize", new Class[]{Point.class}).invoke(defaultDisplay, new Object[]{point});
                int i3 = point.x;
                try {
                    i2 = point.y;
                } catch (Exception unused3) {
                }
                i = i3;
            } catch (Exception unused4) {
            }
        }
        return new int[]{i, i2};
    }
}
