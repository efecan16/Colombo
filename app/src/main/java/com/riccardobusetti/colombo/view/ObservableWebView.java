package com.riccardobusetti.colombo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.riccardobusetti.colombo.R;

public class ObservableWebView extends WebView {

    private int lastY;
    private final int[] scrollOffset = new int[2];
    private final int[] scrollConsumed = new int[2];
    private int nestedOffsetY;
    private NestedScrollingChildHelper childHelper;

    private float startX, endX;

    private boolean canScrollVertically;

    private CustomWebChromeClient webChromeClient;
    private View previous, next;

    public ObservableWebView(Context context) {
        super(context);
        init();
    }

    public ObservableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObservableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        childHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    public void setNavigationViews(View previous, View next) {
        this.previous = previous;
        this.next = next;
    }

    public void setCanScrollVertically(boolean canScrollVertically) {
        this.canScrollVertically = canScrollVertically;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        getRootView().findViewById(R.id.swipe_layout).setEnabled(getScrollY() == 0 && !canScrollVertically);
    }

    @SuppressWarnings("unused")
    public boolean isVideoFullscreen() {
        return webChromeClient != null && webChromeClient.isVideoFullscreen();
    }

    @Override @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client) {
        if (client instanceof CustomWebChromeClient)
            this.webChromeClient = (CustomWebChromeClient) client;
        super.setWebChromeClient(client);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        MotionEvent event = MotionEvent.obtain(ev);
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            nestedOffsetY = 0;
        }

        int eventY = (int) event.getY();
        event.offsetLocation(0, nestedOffsetY);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = lastY - eventY;

                if (dispatchNestedPreScroll(0, deltaY, scrollConsumed, scrollOffset)) {
                    deltaY -= scrollConsumed[1];
                    lastY = eventY - scrollOffset[1];
                    event.offsetLocation(0, -scrollOffset[1]);
                    nestedOffsetY += scrollOffset[1];
                }

                if (dispatchNestedScroll(0, scrollOffset[1], 0, deltaY, scrollOffset)) {
                    event.offsetLocation(0, scrollOffset[1]);
                    nestedOffsetY += scrollOffset[1];
                    lastY -= scrollOffset[1];
                }

                float scrollX = startX - event.getX();
                if ((canGoForward() && scrollX > 0) || (canGoBack() && scrollX < 0))
                    setX(-scrollX / 5);

                if (next != null) next.setPressed(scrollX > getOverscrollDistance());
                if (previous != null) previous.setPressed(scrollX < -getOverscrollDistance());

                break;
            case MotionEvent.ACTION_DOWN:
                lastY = eventY;
                startX = event.getX();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                if (next != null) next.setPressed(false);
                if (previous != null) previous.setPressed(false);

                endX = event.getX();
                animate().x(0).setDuration(50).start();

                if (startX - endX > getOverscrollDistance() && canGoForward()) goForward();
                else if (startX - endX < -getOverscrollDistance() && canGoBack()) goBack();

                stopNestedScroll();
                break;
        }

        return super.onTouchEvent(event);
    }

    private float getOverscrollDistance() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, getResources().getDisplayMetrics());
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        childHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}