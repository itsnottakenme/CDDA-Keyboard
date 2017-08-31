package com.blackcj.customkeyboard.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.blackcj.customkeyboard.LatinKeyboardViewCdda;
import com.blackcj.customkeyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 8/30/2017.
 *
 *
 *  KeyboardView must pass all onTouchEvents here without handling them
 *
 */

public class TouchAreaView_CURRENTLYDISABLED extends View
{
    LatinKeyboardViewCdda mKeyboardView;
    List<Integer> pointerIndexList;

    int[] mPointerCounter;

    public TouchAreaView_CURRENTLYDISABLED(Context context)
    {
        super(context);
        init();
    }

    public TouchAreaView_CURRENTLYDISABLED(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TouchAreaView_CURRENTLYDISABLED(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {

        mKeyboardView = (LatinKeyboardViewCdda)getRootView().findViewById(R.id.base_keyboard_view);
        pointerIndexList = new ArrayList<>();

    }


    /**
     *
     *
     * If pointerIndex  has ACTION_DOWN event in keyboard area, send that event
     * as well as its corresponding  ACTION_UP event to KeyboardView. Otherwise let
     * the next view in line handle it
     * @param me
     * @return
     */
    @Override
    public boolean onTouchEvent (MotionEvent me)
    {
        boolean handled = false;
        int index;

        //what is difference between me.getPointer
        if (me.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            if (isInKeyboard((int)me.getRawX(), (int)me.getRawY()))
            {
                pointerIndexList.add(me.getActionIndex());
                mKeyboardView.onTouchEvent(me);//handleACTION_DOWN(me);
                handled = true;
            }
        }
        else if (pointerIndexList.indexOf(me.getActionIndex())>-1)
        {           //pass on any motion events started in keyboard

            if (me.getActionMasked() == MotionEvent.ACTION_UP)
            {
                index = pointerIndexList.indexOf(me.getActionIndex());
                pointerIndexList.remove(index);
            }

            handled = true;
            mKeyboardView.onTouchEvent(me);
        }


        return handled;
    }

    private boolean isInKeyboard(int rx, int ry) {
        int[] l = new int[2];


        mKeyboardView.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = mKeyboardView.getWidth();
        int h = mKeyboardView.getHeight();  //todo: should be changed to mKeyboardView.getKeyboard().getHeight();


        if (rx < x || rx > x + w || ry < y || ry > y + h) {
            return false;
        }
        return true;
    }



}
