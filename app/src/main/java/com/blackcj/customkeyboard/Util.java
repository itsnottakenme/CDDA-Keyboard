package com.blackcj.customkeyboard;

import android.inputmethodservice.Keyboard;

import java.util.List;

/**
 * Created by ian on 9/1/2017.
 */

public class Util
{
    /**
     * Returns the index of the key
     *
     *
     * in[] of size 0 returned if no key found at (x,y)
     * @param x
     * @param y
     * @param kb
     * @return
     */
    static public Keyboard.Key getKeyAtCoordifExists(int x, int y, Keyboard kb) {
        List<Keyboard.Key> keys = kb.getKeys();
        Keyboard.Key[] mKeys = keys.toArray(new Keyboard.Key[keys.size()]);
        int i = 0;
        for (Keyboard.Key key : mKeys)
        {
            if(key.isInside(x, y))
            {
                return key;
            }
            i++;
        }
        return null;
    }

}
