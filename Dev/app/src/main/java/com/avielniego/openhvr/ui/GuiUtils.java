package com.avielniego.openhvr.ui;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.List;

public class GuiUtils
{
    public static void highlightTextInTextView(TextView textView, @Nullable String textToHighlight, int color)
    {
        String originalText = String.valueOf(textView.getText());
        Spannable wordToSpan = new SpannableString(originalText);

        if (textToHighlight == null || textToHighlight.isEmpty())
        {
            return;
        }

        int i = originalText.toLowerCase().indexOf(textToHighlight.toLowerCase());
        while (i != -1)
        {
            wordToSpan.setSpan(new ForegroundColorSpan(color),
                               i,
                               i + textToHighlight.length(),
                               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            i = originalText.indexOf(textToHighlight, i + 1);
        }

        textView.setText(wordToSpan);
    }

}
