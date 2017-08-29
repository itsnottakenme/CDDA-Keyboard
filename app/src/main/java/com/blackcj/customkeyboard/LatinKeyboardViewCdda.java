package com.blackcj.customkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import java.util.List;
import java.util.Map;

/**
 * Created by ian on 8/27/2017.
 *
 *
 *todo: I'm wondering if this code should be moved to SoftKeyboard as they both use the same listener :P
 *
 *
 *
 */

public class LatinKeyboardViewCdda extends LatinKeyboardView
{


    //These variables are all for mPopupKeyboard management
    private PopupWindow mPopupKeyboard;
    private View vKeyboardViewHolder;
    private KeyboardView mMiniKeyboard;
    private int mPopupLayout;
    private boolean mMiniKeyboardOnScreen;
    private View mPopupParent;
    private int mMiniKeyboardOffsetX;
    private int mMiniKeyboardOffsetY;
    private Map<Keyboard.Key,View> mMapOfKeyAndViewPairs;
    private Keyboard.Key[] mKeys;




    //todo: breakpoints in constructors are never called? why???
    public LatinKeyboardViewCdda(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupListeners();
        init(context, attrs);
    }

    public LatinKeyboardViewCdda(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //assign view for popup keyboard


        setupListeners();
        init(context, attrs);
    }




    private void init(Context context, AttributeSet attrs)
    {


        /**
         *  todo: gets keyboard layout from xml file but too hard so just hardcoding for now
         */
//        TypedArray a = context.obtainStyledAttributes(
//                attrs, android.R.styleable.KeyboardView, defStyleAttr, defStyleRes);
//
//        LayoutInflater inflate =
//                (LayoutInflater) context
//                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        int previewLayout = 0;
//        int keyTextSize = 0;
//
//        int n = a.getIndexCount();
//
//        for (int i = 0; i < n; i++)
//        {
//            int attr = a.getIndex(i);
//
//            switch (attr)
//            {
//                case com.android.internal.R.styleable.KeyboardView_popupLayout:
//                    mPopupLayout = a.getResourceId(attr, 0);
//                    break;
//            }
//        }




        mPopupKeyboard = new PopupWindow(context);
        mPopupKeyboard.setBackgroundDrawable(null);
        mPopupKeyboard.setClippingEnabled(false); //todo: enable to show offscreen :D

    }

    private void setupListeners()    {
        /**
         * todo: This is where I should handle popping up keyboard
         *
         * */
//        setOnKeyboardActionListener(new OnKeyboardActionListener()
//        {
//
//            /**
//             * todo: what the hell does this even do anymore????? I think I am handling this in onTouch()
//             *      now and this can be deleted and the code and SoftKeyboard should be set as
//             *      OnKeyboardActionListener() and should work without modification I think... hehe
//             * @param primaryCode
//             */
//                    @Override
//                    public void onPress(int primaryCode)
//                    {
//
//                        /**
//                         * todo: might be easier to jus CREATE NEW LISTENER that will return Key
//                         *      as opposed to the primary code
//                         */
//                        //todo: Calls popup keyboard using onLongPress()
//                        Keyboard.Key pressedKey= LatinKeyboardViewCdda.this.findPressedKey(primaryCode);
//
//                        if (pressedKey!=null)
//                        {
//                            LatinKeyboardViewCdda.this.onLongPress(pressedKey);
//                        }
//
//
//                        return;
//                    }
//
//                    @Override
//                    public void onRelease(int i)
//                    {
//                        //todo:  closes after pop up disappears. Change so happens after finger lifted
//                        //invalidateAllKeys();
//                    }
//
//                    @Override
//                    public void onKey(int i, int[] ints)
//                    {
//
//                    }
//
//                    @Override
//                    public void onText(CharSequence charSequence)
//                    {
//
//                    }
//
//                    @Override
//                    public void swipeLeft()
//                    {
//
//                    }
//
//                    @Override
//                    public void swipeRight()
//                    {
//
//                    }
//
//                    @Override
//                    public void swipeDown()
//                    {
//
//                    }
//
//                    @Override
//                    public void swipeUp()
//                    {
//
//                    }
//        });

        return;
    }




    @Override
    public boolean onTouchEvent (MotionEvent me)
    {
        //assumption: I am processing ALL TouchEvents
        boolean result= true; //= super.onTouchEvent(me); //allows event to be processed normally
        int[] keyIndexes;
        Keyboard.Key pressedKey;

        LayoutInflater layoutInflater;



        if ( me.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            /**
             * Popup keyboard and centre over pressed key
             *
             *
             */


            keyIndexes= getKeyboard().getNearestKeys((int)me.getX(me.getPointerId(0)), (int)me.getY(me.getPointerId(0)));

            if (keyIndexes!=null)
            {   //pressed key:
                pressedKey= getKeyboard().getKeys().get(keyIndexes[0]);


                //load popup keybaord and setup
                layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (layoutInflater != null)
                {
                    vKeyboardViewHolder = layoutInflater.inflate(R.layout.keyboard_popup_layout, null);
                }

                //but haven't instantiated this kv yet though..
                mMiniKeyboard = (KeyboardView) vKeyboardViewHolder.findViewById(R.id.keyboardView);
                mMiniKeyboard.setOnKeyboardActionListener(null);
                //todo setup listener
//                (new OnKeyboardActionListener()
//                {
//                        public void onKey(int primaryCode, int[] keyCodes)
//                        {
////                            mKeyboardActionListener.onKey(primaryCode, keyCodes);
////                            dismissPopupKeyboard();
////                        }
//
//                        public void onText(CharSequence text) {
////                            mKeyboardActionListener.onText(text);
////                            dismissPopupKeyboard();
////                        }
//
//                        public void swipeLeft() { }
//                        public void swipeRight() { }
//                        public void swipeUp() { }
//                        public void swipeDown() { }
//                        public void onPress(int primaryCode) {
//                            mKeyboardActionListener.onPress(primaryCode);
//                        }
//                        public void onRelease(int primaryCode) {
//                            mKeyboardActionListener.onRelease(primaryCode);
//                        }
//                });



                //mInputView.setSuggest(mSuggest);
                Keyboard keyboard;

                    keyboard = new Keyboard(getContext(), R.xml.cdda_keyboard_expanded);
                mMiniKeyboard.setKeyboard(keyboard);
                mMiniKeyboard.setPopupParent(this);
                vKeyboardViewHolder.measure(
                        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));//this may defeat clipping




                //this will popup the keyboard
                mPopupKeyboard.setContentView(vKeyboardViewHolder);
                mPopupKeyboard.setWidth(vKeyboardViewHolder.getMeasuredWidth());
                mPopupKeyboard.setHeight(vKeyboardViewHolder.getMeasuredHeight());
                //todo: change y so it matches height of original keyboard
                mPopupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, pressedKey.x, pressedKey.y+30);
                //todo: add code from KeyboardView.onLongPress()
            mMiniKeyboardOnScreen = true;
            //mMiniKeyboard.onTouchEvent(getTranslatedEvent(me));
            invalidateAllKeys();


        }



            //todo: use location of pressed key to center PopupWindow


        }
        else if ( me.getActionMasked() == MotionEvent.ACTION_UP)  //todo: me.getActionMasked() is correct way i think
        {     //close popup keyboard
            // only works if finger held down first, not for fats press and release WHY????????

            //need to call sent keyEvent if finger on key
            //handleBack(); //removes popup keyboard

            //make sure the event gets consumed so that key is not pressed if not on keyboard key???
            //PopupWindow will handle keypress... not sure how will conflict here???

            dismissPopupKeyboard();
        }

        return result;  //True if the event was handled, false otherwise.
    }

    private void dismissPopupKeyboard() {
        if (mPopupKeyboard.isShowing()) {
            mPopupKeyboard.dismiss();
            mMiniKeyboardOnScreen = false;
            invalidateAllKeys();
        }
    }



//    //@Override
//    public  boolean onHoverEvent_MOVEthisCODE(MotionEvent me)
//    {
//
//        int[] keyIndexes;
//        int indexOfLastKey=-1;  //todo this needs to be made a class variable..
//                                //if currentKey!=lastKey close last popup and open new one
//
//        if (me.getAction() ==MotionEvent.ACTION_HOVER_ENTER)
//        {       //assert: show popup
//
//        }
//        else if (me.getAction() ==MotionEvent.ACTION_HOVER_EXIT)
//        {       //assert: hide popup
//            //todo:method requires ints but me has floats. WHAT IS THE MEANING????
//            // test to see if float and int are compatible...
//            keyIndexes= getKeyboard().getNearestKeys((int)me.getX(me.getPointerId(0)), (int)me.getY(me.getPointerId(0)));
//
//            //keyIndexes[0] is the index of pressed key
//            //
//            //handleBack(); //closes popup keyboard :DDD
//            /**
//             * could invalidate() but that would happen if finger moved to popup menu too
//             * so test if finger is on popup menu too...
//             *
//             *
//             *
//             */
//
//        }
//
//
//
//
//
//
//
//
//
//        return super.onHoverEvent(me);
//    }





    /**
     * Returns
     * 1) currently PRESSED key
     *  - better option but keys aren't showing as being pressed....
     *  //            if (key.pressed == true)
     * -or-
     * 2) the key who's primary code is givem
     * @param primaryCode
     * @return
     */
     Keyboard.Key findPressedKey(int primaryCode)
    {
        List<Keyboard.Key> kkList;
        kkList= getKeyboard().getKeys(); // this gets keyboard i can query for primaryCode...
        Keyboard.Key pressedKey= null;


        for (Keyboard.Key key : kkList)
        {

            if (primaryCode == key.codes[0])    //todo: this DOES NOT work for keys with codes more than 1
            {
                pressedKey= key;
            }

        }


        return pressedKey;
    }


}
