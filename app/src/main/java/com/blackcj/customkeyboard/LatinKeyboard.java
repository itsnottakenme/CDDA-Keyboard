/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blackcj.customkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class LatinKeyboard extends Keyboard {

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    static class LatinKey extends Keyboard.Key {
        
        public LatinKey(Resources res, Keyboard.Row parent, int x, int y,
                XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }
        
        /**
         * Overriding this method so that we can reduce the target area for the key that
         * closes the keyboard. 
         */
        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }
    }

}
