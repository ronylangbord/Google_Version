package com.trunch.trunch.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trunch.trunch.instances.FoodTag;
import com.trunch.trunch.R;
import com.trunch.trunch.Strings;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by or on 4/3/2015.
 */
public class TagsCompletionView extends TokenCompleteTextView {


    public TagsCompletionView(Context context) {
        super(context);
    }

    public TagsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(Object object) {
        FoodTag foodTag = (FoodTag)object;
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.tag_token, (ViewGroup) TagsCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(foodTag.getTag());
        return view;
    }

    @Override
    protected Object defaultObject(String completionText) {
        return new FoodTag(completionText, Strings.cuisine);
    }
}
