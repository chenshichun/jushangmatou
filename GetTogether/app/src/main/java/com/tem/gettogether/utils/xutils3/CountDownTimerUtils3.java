package com.tem.gettogether.utils.xutils3;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.tem.gettogether.R;


public class CountDownTimerUtils3 extends CountDownTimer {
    private TextView mTextView;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils3(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "s");  //设置倒计时时间
//        mTextView.setBackgroundResource(R.drawable.btn_gry); //设置按钮为灰
//        SpannableString spannableString = new SpannableString(mTextView.getText().toString());
//        ForegroundColorSpan span = new ForegroundColorSpan(Color.rgb(136,136,136));
//        spannableString.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mTextView.setTextColor(Color.rgb(247,46,26));
//        mTextView.setText(spannableString);
    }

    @Override
    public void onFinish() {
        mTextView.setText(R.string.reacquire);
        mTextView.setClickable(true);//重新获得点击
//        mTextView.setBackgroundResource(R.drawable.btn_blue);
        mTextView.setTextColor(Color.rgb(247,46,26));
    }

    public void onCancel(){
        cancel();
        mTextView.setText(R.string.huoquyanzm);
        mTextView.setClickable(true);//重新获得点击
        mTextView.setTextColor(Color.rgb(247,46,26));
    }
}