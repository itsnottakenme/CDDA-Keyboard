package com.blackcj.customkeyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ian on 9/1/2017.
 */

public class PopupKeyboardView extends KeyboardView
{
    private boolean mEnableKeyPreview;
    //private MotionEvent currentMotionEvent;     //to determine which key should show popup
    private int mMotionEventOffsetX,
                mMotionEventOffsetY;




    private Keyboard.Key    mlastKeyOver,       //for determining when pointer moves from key to key
                            mCurrentKeyOver;
    PopupWindow pwKeyPreview;

    public PopupKeyboardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public PopupKeyboardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PopupKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    /**
     * todo: for some reason my key preview code ENABLES PREVIEW in super class. WHY???????
     * @param context
     */


    private void init(Context context)
    {
        if (super.isPreviewEnabled() == true)
        {
            mEnableKeyPreview = true;
            super.setPreviewEnabled(false);
        } else
        {
            mEnableKeyPreview = false;
        }


        pwKeyPreview = new PopupWindow(context);
        pwKeyPreview.setBackgroundDrawable(null);
        pwKeyPreview.setClippingEnabled(false);

        mlastKeyOver= null;      //for determining when pointer moves from key to key
        mCurrentKeyOver= null;

    }

    public void dismissKeyPopup()
    {
        pwKeyPreview.dismiss();
        return;
    }


    @Override
    public void setPreviewEnabled(boolean previewEnabled)
    {

        if (super.isPreviewEnabled() == true)
        {
            super.setPreviewEnabled(false);
        }

        mEnableKeyPreview = previewEnabled;
    }


    @Override
    public boolean isPreviewEnabled()
    {
        return mEnableKeyPreview;
    }


    /**
     * This does not affect inflation but is used to translate MotionEvent Coordinates
     *
     * @param x
     * @param y
     */
    void setMotionEventsOffset(int x, int y)
    {
        mMotionEventOffsetX = x;
        mMotionEventOffsetY = y;
    }


    /**
     * So can keep proper track of key events. Whenever popup disappears from screen this should be called
     */
    public void resetKeyHistory()
    {
        mlastKeyOver= null;
        mCurrentKeyOver= null;
    }



    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        float   transX,  //transformed (x,y) in MotionEvent to
                transY;


        int[] kvPopupCoords = new int[2];
        getLocationOnScreen(kvPopupCoords); // when view offscreen this just returns (0,0)


        //todo: double check this bounding box.. but should be fine
//        if (me.getRawX() > kvPopupCoords[0] && me.getRawX() < kvPopupCoords[0] + getWidth()
//                && me.getRawY() > kvPopupCoords[1] && me.getRawY() < kvPopupCoords[1] + getHeight())
        {

            //todo
            transX = me.getRawX() - mMotionEventOffsetX-getPaddingLeft();

            /**
             *            coordinates are relative to BaseKeyboardView
             *            //todo CandidateView is messing up the positioning of PopupKeyboard
             */
            transY = me.getRawY()-kvPopupCoords[1];//-mMotionEventOffsetY;// -getPaddingBottom();

            mCurrentKeyOver= Util.getKeyAtCoordifExists((int) transX, (int) transY, getKeyboard());


            /**
             * determine keys to be pressed and released. Multitouch case not handled
             *
             * //todo: first press case
             */ //mlastKeyOver==null && mCurrentKeyOver!=null



            if (mlastKeyOver==mCurrentKeyOver)
            {   // key doesn't change
                //do nothing
            }
            else if (mCurrentKeyOver!= null && mlastKeyOver==null )
            {   //first time key press
                getOnKeyboardActionListener().onPress(mCurrentKeyOver.codes[0]);
                mlastKeyOver = mCurrentKeyOver;
            }
            else if (mCurrentKeyOver!=null && mlastKeyOver!=null)
            {  //key is released, new key is pressed
                getOnKeyboardActionListener().onRelease(mlastKeyOver.codes[0]);
                getOnKeyboardActionListener().onPress(mCurrentKeyOver.codes[0]);
                mlastKeyOver = mCurrentKeyOver;
            }
            else if (mCurrentKeyOver==null && mlastKeyOver!=null)
            {   //key is released, no new key pressed
                getOnKeyboardActionListener().onRelease(mlastKeyOver.codes[0]);
                mlastKeyOver = mCurrentKeyOver;
            }



        }

        return true;//meHandled;


    }


    /**
     * For attaching popup to BasekeyboardView since can't open a popup with a popup as a parent
     * How can this be better implemented?
     */
    private  View vParent;
    public void setPreviewBindingParent_hack(View v)
    {
        vParent= v;
    }



    /**
     *      shows popup for mCurrentKey
     */
    public void showKeyPopup()
    {
        LayoutInflater layoutInflater;
        View layout;
        TextView tv;
        int x, y; //coords of popup

        //note: mCurrentKeyOver should never be null here
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null)
        {
            layout= layoutInflater.inflate(R.layout.preview, null);
            tv= (TextView)layout.findViewById(R.id.key_preview_textview);
            tv.setText(mCurrentKeyOver.label);
            //
            //tv.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
            //
            pwKeyPreview.setContentView(layout);
//            pwKeyPreview.setWidth(layout.getMeasuredWidth()+100); //todo: programmatically determine
//            pwKeyPreview.setHeight(layout.getMeasuredHeight()+100);

            pwKeyPreview.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            pwKeyPreview.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);


            /**
             * todo change y value so he same regardless of key row
             */
            x= mCurrentKeyOver.x +mMotionEventOffsetX - (mCurrentKeyOver.width)/2;
            y= 0-mCurrentKeyOver.height;
            pwKeyPreview.showAtLocation(vParent, Gravity.NO_GRAVITY, x, y);

        }

        return;
    }




    @Override
    public void onDraw (Canvas canvas)
    {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(60);
        paint.setColor(Color.LTGRAY);

        List<Keyboard.Key> keys = getKeyboard().getKeys();


//            for (Keyboard.Key key : keys)
//            {
//                if (key.pressed == true /*&& mPreviewEnabled==true*/)
//                {
//                    canvas.drawText(key.label.toString(), key.x + (key.width - 25), key.y - 80, paint);
//                    //todo: failing because draw coordinates are outside of view..... I think a small popup window is best option here
//                    //
//                    //canvas.drawText(key.label.toString(), (key.x + key.width) / 2, key.y - 2 * key.height, paint);
//                }
//            }


//            for (Keyboard.Key key : keys)
//            {
//                if (key.label != null)
//                {
//                    if (key.label.equals("q"))
//                    {
//                        canvas.drawText("1", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("w"))
//                    {
//                        canvas.drawText("2", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("e"))
//                    {
//                        canvas.drawText("3", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("r"))
//                    {
//                        canvas.drawText("4", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("t"))
//                    {
//                        canvas.drawText("5", key.x + (key.width - 25), key.y + 40, paint);
//                    }
//                }
//
//            }
//            for (Keyboard.Key key : keys)
//            {
//                if (key.label != null)
//                {
//                    if (key.label.equals("q"))
//                    {
//                        canvas.drawText("1", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("w"))
//                    {
//                        canvas.drawText("2", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("e"))
//                    {
//                        canvas.drawText("3", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("r"))
//                    {
//                        canvas.drawText("4", key.x + (key.width - 25), key.y + 40, paint);
//                    } else if (key.label.equals("t"))
//                    {
//                        canvas.drawText("5", key.x + (key.width - 25), key.y + 40, paint);
//                    }
//                }
//
//            }
            return;
        }


    }
