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
             */
            transY = me.getRawY()-kvPopupCoords[1];//-mMotionEventOffsetY;// -getPaddingBottom();

            //transY = me.getY();//me.getRawY() + mMotionEventOffsetY - kvPopupCoords[1]-getPaddingBottom();
            mCurrentKeyOver= Util.getKeyAtCoordifExists((int) transX, (int) transY, getKeyboard());


            /**
             * determine keys to be pressed and released. Multitouch case not handled
             */
            if (mlastKeyOver==null && mCurrentKeyOver!=null)
            {
                getOnKeyboardActionListener().onPress(mCurrentKeyOver.codes[0]);
                mlastKeyOver= mCurrentKeyOver;
            }
            else if (mlastKeyOver!=null)
            {
                if (mCurrentKeyOver==null)
                {
                    getOnKeyboardActionListener().onRelease(mlastKeyOver.codes[0]);
                    mlastKeyOver= mCurrentKeyOver;

                }
                else    //assert: (mCurrentKeyOver!=null)
                {       //new key pressed
                    if (mCurrentKeyOver!=mlastKeyOver)      //assert key has changed
                    {
                        getOnKeyboardActionListener().onRelease(mlastKeyOver.codes[0]);
                        getOnKeyboardActionListener().onPress(mCurrentKeyOver.codes[0]);
                        mlastKeyOver = mCurrentKeyOver;
                    }
                }
            }
            else
            {
                //do nothing
            }



        }

        return true;//meHandled;


    }




//    //    @Override
//    public boolean onTouchEvent_OLD_AND_CRAPPY(MotionEvent me)
//    {
//        float transX,  //transformed (x,) in MotionEvent to
//                transY;
//        MotionEvent transMe= null;
//        int keyIndex;
//        //Keyboard.Key keyUnderPointer = null;
//        boolean meHandled= true;
//        Keyboard.Key key;       //key that is being hovered over
//
//        /**
//         * Todo further process MotionEvent (or create new one!) before passing to kvPopup
//         * 1) Are (x,y) coords ok or do they need to be transformed?
//         * 2) Only send events in bounds of kvPopup?
//         *          YES TOO MANY EVENTS BEING SENT TO kvpopup
//         */
//        //for debug TOAST messages only
//        float mX = me.getX();
//        float mY = me.getY();
//        float mRawX = me.getRawX();
//        float mRawY = me.getRawY();
//
//        float dog = mX + mY + mRawX + mRawY;
//
//
//        int[] kvPopupCoords = new int[2];
//        getLocationOnScreen(kvPopupCoords); // when view offscreen this just returns (0,0)
//
//
//        //todo: double check this bounding box.. but should be fine
//        if (me.getRawX() > kvPopupCoords[0] && me.getRawX() < kvPopupCoords[0] + getWidth()
//                && me.getRawY() > kvPopupCoords[1] && me.getRawY() < kvPopupCoords[1] + getHeight())
//        {// assert: MotionEvent occurred in popup keyboard
//            ////////////////////////////
//            //Create new MotionEvent with translated coords
//            ///////////////////////////////
//
//
//            int[] location = new int[2];
//            getLocationOnScreen(location);
//
//            transX = me.getRawX() - mMotionEventOffsetX-getPaddingLeft();
//            mTransX= (int)transX;
//            transY = me.getRawY() + mMotionEventOffsetY - kvPopupCoords[1]-getPaddingBottom();
//
//
//            //todo:
//            key= Util.getKeyAtCoordifExists((int) transX, (int) transY, getKeyboard());
//
//            if (key!=null)
//            {       //assert: MotionEvent is within a keys boundaries
//                mCurrentKeyOver= key;
//
//                //decide which key to send
//                if (mlastKeyOver== null)
//                {       //I guess the ACTION_DOWN is on a region outside the key
//                    transMe = MotionEvent.obtain(me.getDownTime(), me.getEventTime(), MotionEvent.ACTION_DOWN, transX, transY, 0);
//                    showKeyPopup(me);
//                }
//                else if (mlastKeyOver == mCurrentKeyOver) //should be ok since same references
//                {
//                    //nothing has changed so no updates needed
//                }
//
//                else if (mCurrentKeyOver != mlastKeyOver)
//                {   //key has changed so send new MotionEvent
//                    //todo how to unpress previous key? is it needed?
//                    transMe = MotionEvent.obtain(me.getDownTime(), me.getEventTime(), MotionEvent.ACTION_MOVE, transX, transY, 0);
//                    showKeyPopup(me);
//                }
//
//                //mlastKeyOver= key;
//
//            }
//            else //assert: key is null
//            {       //create MotionEvent to unpress key
//
//                //causes key never to highlight:
//                //mCurrentKeyOver= null;
//                //transMe = MotionEvent.obtain(me.getDownTime(), me.getEventTime(), MotionEvent.ACTION_MOVE, transX, transY, 0);
//            }
//
//            if (transMe!=null)
//            {   //transmit modified MotionEvent if touch is within key boundaries
//                //todo: superclass implementation (of getNearestKeys() is DUMB. DO NOT RELY ON to press keys
//                //press them myself!!!!!!!!!!!
//                meHandled = super.onTouchEvent(transMe);            //this causes key to be highlighted
//            }
//            else
//            {
//
//            }
//            mlastKeyOver= key;
//        }
//        else
//        {
//            mlastKeyOver= null;
//        }
//
//            return true;//meHandled;
//
//
//    }





     private  View vParent;
    public void setPreviewBindingParent_hack(View v)
    {
        vParent= v;
    }


    /**
     * todo: Popup window never shown. WHY???????????????
     */

//    private void showKeyPopup(MotionEvent me)
//    {
//
//    }

    /**
     * todo: I can't figure out correct coordinates for showing popup so I will try  onDraw() again
     * @param me
     */
    private void showKeyPreview_OLD(MotionEvent me)
    {

        LayoutInflater layoutInflater;
        View layout;
        TextView tv;


            //load popup keybaord and setup
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null)
            {
                layout= layoutInflater.inflate(R.layout.preview, null);
                tv= (TextView)layout.findViewById(R.id.key_preview_textview);
                tv.setText(mCurrentKeyOver.label);
                pwKeyPreview.setContentView(layout);
                pwKeyPreview.setWidth(layout.getMeasuredWidth()+100);
                pwKeyPreview.setHeight(layout.getMeasuredHeight()+100);

                ///////////////
                int keyX= mCurrentKeyOver.x;
                int keyY= mCurrentKeyOver.y;
                int x= (int)me.getRawX(), y=layout.getMeasuredHeight(), z=x+y+keyX+keyY;
                Keyboard.Key kkk= mCurrentKeyOver; kkk.isInside(1,1);
               ////////////////
                //mCurrentKeyOver=???? does it change? YEP. CORRECT KEYS BEING PASSED BUT THE POPUP DOESN CHANGE HMM....



                pwKeyPreview.showAtLocation(vParent, Gravity.NO_GRAVITY,
                        /**(int)me.getRawX()**/ mCurrentKeyOver.x +mMotionEventOffsetX - (mCurrentKeyOver.width)/2,
                                            mCurrentKeyOver.y - 2 * mCurrentKeyOver.height);
                //invalidateAllKeys(); //what does it do???
            }            //transX = me.getRawX() - mMotionEventOffsetX;


    }

    /**
     *      shows popup for mCurrentKey
     */
    public void showKeyPopup()
    {
        LayoutInflater layoutInflater;
        View layout;
        TextView tv;

        //note: mCurrentKeyOver should never be null here
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null)
        {
            layout= layoutInflater.inflate(R.layout.preview, null);
            tv= (TextView)layout.findViewById(R.id.key_preview_textview);
            tv.setText(mCurrentKeyOver.label);
            pwKeyPreview.setContentView(layout);
            pwKeyPreview.setWidth(layout.getMeasuredWidth()+100);
            pwKeyPreview.setHeight(layout.getMeasuredHeight()+100);


            /**
             * todo change y value so he same regardless of key row
             */
            pwKeyPreview.showAtLocation(vParent, Gravity.NO_GRAVITY,
                    mCurrentKeyOver.x +mMotionEventOffsetX - (mCurrentKeyOver.width)/2,
                    0-mCurrentKeyOver.height);

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
