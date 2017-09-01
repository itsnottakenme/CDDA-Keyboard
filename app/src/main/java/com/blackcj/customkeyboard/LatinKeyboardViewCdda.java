package com.blackcj.customkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
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
    private boolean mIsMiniKeyboardOnScreen;
    private View mPopupParent;
    private int mkvPopupOffsetX;
    private int mkvPopupOffsetY;
    private Map<Keyboard.Key,View> mMapOfKeyAndViewPairs;
    private Keyboard.Key[] mKeys;
    //Context mContext;

//debug printing toasts ONLY
//    private float   mX,
//                    mY,
//                    mRawX,
//                    mRawY;

    private boolean mKeyPressEnabled; // todo: differentiates between sending KeyEvent or not
                                        // do for popup keyboard but not base

    private List<Integer> pointerIndexList; //for keeping track of ACTION_UP, MOTION, and DOWN tuples


    //todo: breakpoints in constructors are never called? why???
    public LatinKeyboardViewCdda(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupListeners();
        init(context, attrs);
    }

    public LatinKeyboardViewCdda(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        pointerIndexList= new ArrayList<Integer>();
//false
    }

    private void setupListeners()    {
        /**
         * No need for this???
         *it might be easier if kvPopup
         *  has its own onTouchEvent though...
         * */

        return;
    }


    /**
     * todo: ALL events must be processed here and sent to appropriate listener
     * todo: crashes when more than one pointer touches screen... Make counter so only first ACTION_DOWN is processed
     *
     *
     *
     * @param me
     * @return
     */

    @Override
    public boolean onTouchEvent (MotionEvent me)
    {
        boolean result= true; //= super.onTouchEvent(me); //allows event to be processed normally
        int index;


        /**
         * Instead of relying on CandidateView to get coordinates
         * try:
         *  ABSOLUTE_X= (ACTION_DOWN).getRawX()+(ACTION_UP).getX()
         */


        if ( me.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            if (pointerIndexList.size() == 0)
            {   //assert: only first finger is processed. Others are ignored
                pointerIndexList.add(me.getActionIndex());
                popupKeyboard(me);
            }

        }
        else if ( me.getActionMasked() == MotionEvent.ACTION_UP)  //todo: me.getActionMasked() is correct way i think
        {
            index = pointerIndexList.indexOf(me.getActionIndex());
            if (index>-1)       //only process MotionEvents whose ACTION_DOWNS have been processed
            {
                pointerIndexList.remove(index);
                sendKeyandDismissKeyboard(me);
            }
        }
        else if (me.getActionMasked() == MotionEvent.ACTION_MOVE)
        {
            //does this catch original ACTION_MOVE as well
            popupKeyPreview(me);
        }
        else //todo: what to do with ACTION_CANCEL and others??
        {

        }



        return true;//result;// false;//result;  //True if the event was handled, false otherwise.
    }


    private void popupKeyPreview(MotionEvent me)
    {
        float   transX,  //transformed (x,) in MotionEvent to
                transY;
        MotionEvent transMe;

        /**
         * Todo further process MotionEvent (or create new one!) before passing to kvPopup
         * 1) Are (x,y) coords ok or do they need to be transformed?
         * 2) Only send events in bounds of kvPopup?
         *          YES TOO MANY EVENTS BEING SENT TO kvpopup
         */
        //for debug TOAST messages only
        float mX=me.getX();
        float mY=me.getY();
        float mRawX=me.getRawX();
        float mRawY=me.getRawY();

        float dog= mX+mY+mRawX+mRawY;




        int[] kvPopupCoords= new int[2];
        kvPopup.getLocationOnScreen(kvPopupCoords); // when view offscreen this just returns (0,0)

        if (    me.getRawX()>kvPopupCoords[0] && me.getRawX()<kvPopupCoords[0]+kvPopup.getWidth()
             && me.getRawY()>kvPopupCoords[1]  && me.getRawY()<kvPopupCoords[1]+kvPopup.getHeight() )
        {// assert: MotionEvent ocurred in popup keyboard
            ////////////////////////////
            //todo: create new MotionEvent with translated coords
            ///////////////////////////////
            transX= me.getRawX()-mkvPopupOffsetX; //these coords look correct
            transY= me.getRawY()+mkvPopupOffsetY-kvPopupCoords[1];

            transMe= MotionEvent.obtain(me.getDownTime(), me.getEventTime(), MotionEvent.ACTION_DOWN, transX, transY, 0);


            kvPopup.onTouchEvent(transMe);

        }



    //create MotionEvent and sent to kvPopup






        //only start sending once pointer is inside bounds of popup



        return;
    }


    private void popupKeyboard(MotionEvent me)
    {
        boolean result= true; //= super.onTouchEvent(me); //allows event to be processed normally
        int[] keyIndexes;
        Keyboard keyboard;
      //  Keyboard.Key pressedKey;
        LayoutInflater layoutInflater;

        /**
         * Popup keyboard and centre over pressed key
         */
        keyIndexes= getKeyboard().getNearestKeys((int)me.getX(me.getPointerId(0)), (int)me.getY(me.getPointerId(0)));
        if (keyIndexes!=null)
        {   //pressed key:
         //   pressedKey= getKeyboard().getKeys().get(keyIndexes[0]);


            //load popup keybaord and setup
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null)
            {
                vKeyboardViewHolder = layoutInflater.inflate(R.layout.keyboardview_popup, null);
            }


            kvPopup = (KeyboardView) vKeyboardViewHolder.findViewById(R.id.popup_keyboard_view);

            kvPopup.setOnKeyboardActionListener(new OnKeyboardActionListener()
                {
                        List<Integer> mPressedKeys = new ArrayList<Integer>();
                        Map<Integer, Integer> primaryCodetoKeyIndexMap;

                        /**
                         * Press key down on keyboard to activate popup
                         * todo: is this code currently executing?? YES!!! but no press is passed
                         * @param primaryCode
                         */

                        @Override
                        public void onPress(int primaryCode)
                        {
                            List<Keyboard.Key> keys = kvPopup.getKeyboard().getKeys();
                            Keyboard.Key pressedKey;// = keys.get(keys.indexOf(primaryCode));


                            //build up hashmap
                            if (primaryCodetoKeyIndexMap==null)
                            {
                                primaryCodetoKeyIndexMap = new HashMap<Integer, Integer>();
                                for (int i=0; i<keys.size(); i++)
                                {
                                    //todo; this needs to fixed to work with multiple keycodes
                                    primaryCodetoKeyIndexMap.put(keys.get(i).codes[0], i);
                                }
                            }

                            //process primaryCode
                            if (primaryCode != NOT_A_KEY && primaryCode!= KeyEvent.KEYCODE_UNKNOWN)
                            {           //assert its a valid keycode

                                /**
                                 * primaryCodetoKeyIndexMap doesn't work!!!
                                 * Threadign issue possibly?????
                                 * break into separate lines of code!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                 */

                                Integer keyIndex= primaryCodetoKeyIndexMap.get((Integer)primaryCode);
                            // try cast as Integer?????????????????????????????
                                if (keyIndex!=null && keys.get(keyIndex)!=null)
                                {
                                    pressedKey = keys.get( keyIndex);
                                    pressedKey.pressed= true;
                                    mPressedKeys.add(primaryCode);

                                }
                            }
                            return;
                        }

                        @Override
                        public void onRelease(int primaryCode)
                        {
                            if (primaryCode != NOT_A_KEY)
                            {
                                List<Keyboard.Key> keys = kvPopup.getKeyboard().getKeys();


                                Keyboard.Key releasedKey =keys.get(primaryCodetoKeyIndexMap.get(primaryCode));

                                releasedKey.pressed = false;
                                mPressedKeys.remove(primaryCode);
                            }
                            return;
                        }

                        @Override
                        public void onKey(int primaryCode, int[] keyCodes)
                        {

                        }

                        @Override
                        public void onText(CharSequence text)
                        {

                        }

                        @Override
                        public void swipeLeft()
                        {

                        }

                        @Override
                        public void swipeRight()
                        {

                        }

                        @Override
                        public void swipeDown()
                        {

                        }

                        @Override
                        public void swipeUp()
                        {

                        }
                    });


            keyboard = new Keyboard(getContext(), R.xml.cdda_keyboard_popup);
            kvPopup.setKeyboard(keyboard);
            kvPopup.setPopupParent(this);       //kvPopup determines coordinates relative to parent
            kvPopup.setPreviewEnabled(true);        //todo:preview not showing... why?

            //TODO: Working BUT NEED get better value than 400!!!!!!
            vKeyboardViewHolder.measure(
                    MeasureSpec.makeMeasureSpec(getKeyboard().getMinWidth()+400, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(getKeyboard().getHeight(), MeasureSpec.AT_MOST));//this may defeat clipping

            mPopupKeyboard.setContentView(vKeyboardViewHolder);
            mPopupKeyboard.setWidth(vKeyboardViewHolder.getMeasuredWidth());
            mPopupKeyboard.setHeight(vKeyboardViewHolder.getMeasuredHeight());





            ////////CALCULATING POPUP KEYBOARD OFFSET//////////
            int KeyboardWidth= getWidth();
            int popupWidth= mPopupKeyboard.getWidth();
            float percentHorizontalOffset = me.getRawX()/(float)getWidth();
            int horizontalOffsetPopup= (int)(percentHorizontalOffset*mPopupKeyboard.getWidth());
            int horizontalOffsetBase= (int)me.getX();//(int)percentHorizontalOffset*mPopupKeyboard.getWidth();
            //subtract the distance

            mkvPopupOffsetX=(int)me.getRawX()-horizontalOffsetPopup;

            mkvPopupOffsetY= 50 ; //todo:make this a multiple of key height.its influenced by CandidateView
            //////////////////////////////////////////////////////////////////




            mPopupKeyboard.showAtLocation( this, Gravity.NO_GRAVITY, mkvPopupOffsetX, mkvPopupOffsetY);
            mIsMiniKeyboardOnScreen = true;
            invalidateAllKeys();
        }
        return;
    }

    private void sendKeyandDismissKeyboard(MotionEvent me)
    {
        boolean result= true; //= super.onTouchEvent(me); //allows event to be processed normally
        int[] keyIndexes;
        Keyboard keyboard;
        Keyboard.Key pressedKey;
        LayoutInflater layoutInflater;


        int[] kvPopupOffsets= new int[2];
        kvPopup.getLocationOnScreen(kvPopupOffsets); // when view offscreen this just returns (0,0)
        //kvPopup.getLocationInWindow(kvPopupOffsets);


        int     touchX= (int) me.getRawX() - mkvPopupOffsetX - kvPopup.getPaddingLeft();
        int     touchY= (int) me.getRawY() - kvPopupOffsets[1];//- kvPopupOffsets[1]  - kvPopup.getPaddingTop();




        //NOTE Keyboard.getNearestKeys() is broken. This is a hack fix for that
        keyIndexes= getNearestKeys(touchX, touchY, kvPopup.getKeyboard());//kvPopup.getKeyboard(). getNearestKeys(touchX, touchY);

        if (keyIndexes!=null && keyIndexes.length>0)
        {   //pressed key:
            pressedKey = kvPopup.getKeyboard().getKeys().get(keyIndexes[0]);
            getOnKeyboardActionListener().onKey(pressedKey.codes[0], pressedKey.codes);
        }

        dismissPopupKeyboard();

        return;
    }







    public void handleACTION_DOWN(MotionEvent me)
    {
        //copy and paste all ACTION_DOWN code
        //leave onTouchEvent() empty so it overrides any default behavior
    }

    public void handleACTION_UP(MotionEvent me)
    {
        //copy and paste all ACTION_UP code
    }























    /**
     *
     * @param sText
     */

    public void showToastInIntentService(final String sText) {
        final Context MyContext = getContext();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_SHORT);
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
            mIsMiniKeyboardOnScreen = false;
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
    //todo: modify so no key is selected if values are outside view
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


}       //////END CLASS/////
