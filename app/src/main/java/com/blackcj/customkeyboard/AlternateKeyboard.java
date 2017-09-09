package com.blackcj.customkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.support.annotation.XmlRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 9/1/2017.
 */

public class AlternateKeyboard extends Keyboard
{
    Map<Integer, Integer> mPrimaryCodetoKeyIndexMap; //TO QUICKLY FIND A KEY


    public AlternateKeyboard(Context context, int xmlLayoutResId)
    {
        super(context, xmlLayoutResId);
        init();
    }

    public AlternateKeyboard(Context context, @XmlRes int xmlLayoutResId, int modeId, int width, int height)
    {
        super(context, xmlLayoutResId, modeId, width, height);
        init();
    }

    public AlternateKeyboard(Context context, @XmlRes int xmlLayoutResId, int modeId)
    {
        super(context, xmlLayoutResId, modeId);
        init();
    }

    public AlternateKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding)
    {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        init();
    }


    /**
     * Map for quick finding of keycode -> Keyboard.Key
     */


    void init()
    {
        List<Keyboard.Key> keys = getKeys();

        //build up hashmap of primaryCode-> Keyboard.Key
        mPrimaryCodetoKeyIndexMap = new HashMap<Integer, Integer>();
        for (int i=0; i<keys.size(); i++)
        {
            //todo; this needs to fixed to work with multiple keycodes
            mPrimaryCodetoKeyIndexMap.put(keys.get(i).codes[0], i);
        }

        return;
    }






    /**
     *
     * todo: implement findKeyByCoord() here. Current code in Util.getKeyAtCoordifExists()
     *
     *
     */








}
