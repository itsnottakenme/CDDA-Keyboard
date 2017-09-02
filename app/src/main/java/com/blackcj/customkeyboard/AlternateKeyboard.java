package com.blackcj.customkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.support.annotation.XmlRes;

/**
 * Created by ian on 9/1/2017.
 */

public class AlternateKeyboard extends Keyboard
{
    public AlternateKeyboard(Context context, int xmlLayoutResId)
    {
        super(context, xmlLayoutResId);
    }

    public AlternateKeyboard(Context context, @XmlRes int xmlLayoutResId, int modeId, int width, int height)
    {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public AlternateKeyboard(Context context, @XmlRes int xmlLayoutResId, int modeId)
    {
        super(context, xmlLayoutResId, modeId);
    }

    public AlternateKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding)
    {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }


    /**
     *
     * todo: implement findNearestKeys here.
     *
     */








}
