package com.blackcj.customkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by ian on 8/27/2017.
 *
 *
 *todo: I'm wondering if this code should be moved to SoftKeyboardIME as they both use the same listener :P
 *
 *
 *
 */

public class LatinKeyboardViewCdda extends LatinKeyboardView
{

    private static final int NOT_A_KEY = -1;
    private static int MAX_NEARBY_KEYS = 12;


    //These variables are all for mPopupKeyboard management
    private PopupWindow mPopupKeyboard;
    private View vKeyboardViewHolder;
    private KeyboardView kvPopup;
    private int mPopupLayout;
    private boolean mMiniKeyboardOnScreen;
    private View mPopupParent;
    private int mMiniKeyboardOffsetX;
    private int mMiniKeyboardOffsetY;
    private Map<Keyboard.Key,View> mMapOfKeyAndViewPairs;
    private Keyboard.Key[] mKeys;
    //Context mContext;

    //debug printing toasts ONLY
    private float   mX,
                    mY,
                    mRawX,
                    mRawY;

    private boolean mKeyPressEnabled; // todo: differentiates between sending KeyEvent or not
                                        // do for popup keyboard but not base




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
//             *      now and this can be deleted and the code and SoftKeyboardIME should be set as
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


    /**
     * todo: ALL events must be processed here and sent to appropriate listener
     *
     *
     *
     * @param me
     * @return
     */

    @Override
    public boolean onTouchEvent (MotionEvent me)
    {
        //assumption: I am processing ALL TouchEvents
        boolean result= true; //= super.onTouchEvent(me); //allows event to be processed normally
        int[] keyIndexes;
        Keyboard.Key pressedKey;




        LayoutInflater layoutInflater;

        //for debug TOAST messages only
        mX=me.getX();
        mY=me.getY();
        mRawX=me.getRawX();
        mRawY=me.getRawY();



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
                    vKeyboardViewHolder = layoutInflater.inflate(R.layout.keyboardview_popup, null);
                }

                //but haven't instantiated this kv yet though..
                kvPopup = (KeyboardView) vKeyboardViewHolder.findViewById(R.id.keyboardView);
                kvPopup.setOnKeyboardActionListener(null); // ACTION_UP HANDLES THE LISTENER DUTIES BELOW
                /**
                 * Perhaps if I override onTouchEvent() for kvPopup... calling super constructor to do normal
                 * behavior as well... and only override the ACTION_UP to send KeyEvent... it maybe possible
                 * to have kvPopup handle its own events hmm...                 */
                //todo setup listener. Maybe can stay null as currently base keyboard is delivering the messages
//                (new OnKeyboardActionListener()
//                {
//                        public void onKey(int primaryCode, int[] keyCodes)
//                       {
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

                    keyboard = new Keyboard(getContext(), R.xml.cdda_keyboard_popup);
                kvPopup.setKeyboard(keyboard);
                kvPopup.setPopupParent(this);
                kvPopup.setPreviewEnabled(true);        //todo:preview not showing... why?

                //todo: keyboard doesnt popup without this hmm...
                vKeyboardViewHolder.measure(
                        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));//this may defeat clipping




                //this will popup the keyboard
                mPopupKeyboard.setContentView(vKeyboardViewHolder);
                mPopupKeyboard.setWidth(vKeyboardViewHolder.getMeasuredWidth());
                mPopupKeyboard.setHeight(vKeyboardViewHolder.getMeasuredHeight());
                //todo: change y so it matches height of original keyboard
                //todo: center keyboard over key
                //mPopupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, pressedKey.x, pressedKey.y-400);
                mPopupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, 0, 0);
                //todo: add code from KeyboardView.onLongPress()

            mMiniKeyboardOnScreen = true;
            //kvPopup.onTouchEvent(getTranslatedEvent(me));
            invalidateAllKeys();
            }
        }
        else if ( me.getActionMasked() == MotionEvent.ACTION_UP)  //todo: me.getActionMasked() is correct way i think
        {//todo is this code better here or in listener for kvPopup?
            //todo me.getRawX() vs me.getX WHICH ONE TO USE?????


            int[] kvPopupOffsets= new int[2];
            //kvPopup.getLocationInWindow(kvPopupOffsets); //gives same output as absolute coords
            int rawX= (int)me.getRawX(),
                rawY= (int)me.getRawY();

            kvPopup.getLocationOnScreen(kvPopupOffsets);

            int pointerIndex= me.getActionIndex();
            int     touchX= (int) me.getRawX() - (int)kvPopupOffsets[0] - kvPopup.getPaddingLeft(),
                    touchY= (int) me.getRawY() - (int)kvPopupOffsets[1] - kvPopup.getPaddingTop();
//                if (touchY >= -mVerticalCorrection)
//                    touchY += mVerticalCorrection;




            // get KeyEvent data from kvPopup, send it and then close popup keyboard
            //todo: Keyboard.getNearestKeys(). This is a hack fix for that
            keyIndexes= getNearestKeys(touchX, touchY, kvPopup.getKeyboard());//kvPopup.getKeyboard(). getNearestKeys(touchX, touchY);

            if (keyIndexes!=null && keyIndexes.length>0)
            {   //pressed key:
                pressedKey = kvPopup.getKeyboard().getKeys().get(keyIndexes[0]);
                getOnKeyboardActionListener().onKey(pressedKey.codes[0], pressedKey.codes);
            }

            //make sure the event gets consumed so that key is not pressed if not on keyboard key???
            //PopupWindow will handle keypress... not sure how will conflict here???
            showToastInIntentService("trans:("+(int)touchX+", "+(int)touchY+") raw:("+(int)mRawX+","+(int)mRawY+") action_up");
            dismissPopupKeyboard();

        }

        return result;  //True if the event was handled, false otherwise.
    }

    public void showToastInIntentService(final String sText) {
        final Context MyContext = getContext();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_LONG);
                toast1.show();
            }
        });
    };




    public int[] getNearestKeys(int x, int y, Keyboard kb) {
        List<Keyboard.Key> keys = kb.getKeys();
        Keyboard.Key[] mKeys = keys.toArray(new Keyboard.Key[keys.size()]);
        int i = 0;
        for (Keyboard.Key key : mKeys) {
            if(key.isInside(x, y))
                return new int[]{i};
            i++;
        }
        return new int[0];
    }


    /**
     * Closes popup keyboard
     */
    private void dismissPopupKeyboard()
    {
        if (mPopupKeyboard.isShowing())
        {
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
