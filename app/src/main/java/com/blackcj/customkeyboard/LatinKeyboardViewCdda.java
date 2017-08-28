package com.blackcj.customkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.util.List;

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
    //OnKeyboardActionListener mOnKeyboardActionListener;


    //todo: breakpoints in constructors are never called? why???
    public LatinKeyboardViewCdda(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupListeners();
    }

    public LatinKeyboardViewCdda(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupListeners();
    }

    private void setupListeners()    {
        /**
         * todo: This is where I should handle popping up keyboard
         *
         * */
        setOnKeyboardActionListener(new OnKeyboardActionListener()
        {
                    @Override
                    public void onPress(int primaryCode)
                    {
                        /**
                         * todo: might be easier to jus CREATE NEW LISTENER that will return Key
                         *      as opposed to the primary code
                         */
                        //todo: Calls popup keyboard using onLongPress()
                        Keyboard.Key pressedKey= LatinKeyboardViewCdda.this.findPressedKey(primaryCode);

                        if (pressedKey!=null)
                        {
                            LatinKeyboardViewCdda.this.onLongPress(pressedKey);
                        }


                        return;
                    }

                    @Override
                    public void onRelease(int i)
                    {
                        //todo:  closes after pop up disappears. Change so happens after finger lifted
                        //invalidateAllKeys();
                    }

                    @Override
                    public void onKey(int i, int[] ints)
                    {

                    }

                    @Override
                    public void onText(CharSequence charSequence)
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

        return;
    }





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
