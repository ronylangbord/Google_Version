package com.milab.trunch.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milab.trunch.Strings;
import com.milab.trunch.instances.FoodTag;
import com.milab.trunch.R;
import com.milab.trunch.instances.Restaurant;
import com.milab.trunch.utilities.SharedPrefUtils;
import com.milab.trunch.utilities.TokenViewUtils;
import com.milab.trunch.services.TrunchCheckerService;
import com.milab.trunch.instances.User;
import com.milab.trunch.components.CustomDialogClass;
import com.milab.trunch.components.HorizontialListView;
import com.milab.trunch.components.TagsCompletionView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.milab.trunch.R.id.white_bar;


public class SecondActivity extends ActionBarActivity implements TokenCompleteTextView.TokenListener {

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    TagsCompletionView mTagsCompletionView;
    HorizontialListView mRestContainer;
    FoodTag[] foodTags;
    Restaurant[] restTotal;
    ArrayList<Restaurant> restAdapterList;
    ArrayAdapter<Restaurant> restAdapter;
    ArrayAdapter<FoodTag> foodTagAdapter;
    ObjectMapper mMapper;
    InputMethodManager mInputManger;
    Typeface robotoFont;
    ActionBar actionBar;
    Toolbar mToolbar;
    User mUser;
    //=========================================
    //				Activity Lifecycle
    //=========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_1);


        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.silver_medal);
        mToolbar.setLogo(R.drawable.trunch_logo_small);
        mUser = (User) getIntent().getParcelableExtra(Strings.user);
        loadUserImage();
        // Init Fields
        mSharedPreferences = getSharedPreferences(SharedPrefUtils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mTagsCompletionView = (TagsCompletionView) findViewById(R.id.searchView);
        mRestContainer = (HorizontialListView) findViewById(R.id.restContainer);
        mMapper = new ObjectMapper();
        mInputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        robotoFont  = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");


        // parse json from SharedPref and init all components
        parseAndInit(SharedPrefUtils.getRests(mSharedPreferences)
                , SharedPrefUtils.getFoodTags(mSharedPreferences));

    }



    //=========================================
    //				Private Methods
    //=========================================

    private void loadUserImage() {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageBitmap(bitmap);
                Drawable image = imageView.getDrawable();
                mToolbar.setNavigationIcon(image);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(this).load(mUser.getPictureUrl()).into(target);
    }


    private void parseAndInit(String jsonRest, String jsonTags) {
        // parse
        parseJsonRest(jsonRest);
        parseJsonTags(jsonTags);
        //init
        initRestContainer();
        initTokenView();
        adjustTokenView();
    }

    // for TrunchCheckerService
    public static PendingIntent getSyncPendingIntent(Context context) {
        Intent intent = new Intent(context, TrunchCheckerService.class);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }


    //=========================================
    //			Json Parser
    //=========================================


    private void parseJsonRest(String jsonRest) {
        try {
            restTotal = mMapper.readValue(jsonRest,Restaurant[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void parseJsonTags(String json) {
        try {
            foodTags = mMapper.readValue(json,FoodTag[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=========================================
    //		    HorizontalListView
    //=========================================

    private void initRestContainer() {
        restAdapterList = new ArrayList<>();
        restAdapter = new ArrayAdapter<Restaurant>(this, R.layout.rest_item, restAdapterList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Context context = parent.getContext();
                View retval = LayoutInflater.from(context).inflate(R.layout.rest_item, null);
                ImageButton restBtn = (ImageButton) retval.findViewById(R.id.imageButton);
                TextView restName = (TextView) retval.findViewById(R.id.restName);

                //setting the "choose a restaurant" string and creating a shadow
                TextView mChooseRest = (TextView) findViewById(R.id.choose_a_rest);
                mChooseRest.setTypeface(robotoFont);
                int radius = 15;
                int xOffSet = 10;
                int yOffSet = 10;
                int shadowColor = Color.BLACK;
                mChooseRest.setShadowLayer(radius, xOffSet, yOffSet, shadowColor);


                ImageView mWhiteBar = (ImageView) findViewById(white_bar);
                mChooseRest.setVisibility(View.VISIBLE);
                mWhiteBar.setVisibility(View.VISIBLE);
                final Restaurant rest = getItem(position);
                restName.setText(rest.getName());
                String imgName = rest.getName().toLowerCase().replaceAll(" ", "_");
                int path = getResources().getIdentifier(imgName, "drawable", getPackageName());
                restBtn.setImageResource(path);
                //Picasso.with(context).load(rest.getImage()).resize(350,350).into(restBtn);
                return retval;
            }
        };
        mRestContainer.setAdapter(restAdapter);
        mRestContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                TextView restTitle = (TextView) view.findViewById(R.id.restName);
                final String restName = (String) restTitle.getText();
                CustomDialogClass cdd = new CustomDialogClass(SecondActivity.this, restName);
                cdd.show();
            }

            ;


        });
    }


    //=========================================
    //	            TokenView
    //=========================================

            private void initTokenView() {
                foodTagAdapter = new FilteredArrayAdapter<FoodTag>(this, R.layout.food_tag_layout, foodTags) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {

                            LayoutInflater l =
                                    (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                            convertView = l.inflate(R.layout.food_tag_layout, parent, false);
                        }

                        FoodTag ft = getItem(position);
                        ((TextView)convertView.findViewById(R.id.name)).setText(ft.getTag());
                        ((TextView)convertView.findViewById(R.id.rest)).setText(ft.getType());

                        return convertView;
                    }

                    @Override
                    protected boolean keepObject(FoodTag obj, String mask) {
                        mask = mask.toLowerCase();
                        int secondWord = obj.getTag().indexOf(" ") + 1;
                        return obj.getTag().toLowerCase().startsWith(mask) || obj.getTag().toLowerCase().startsWith(mask,secondWord);
                    }
                };


            }

            private void adjustTokenView() {
                mTagsCompletionView.setAdapter(foodTagAdapter);
                mTagsCompletionView.setTokenListener(this);
                mTagsCompletionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
                mTagsCompletionView.allowDuplicates(false);
                mTagsCompletionView.setCursorVisible(true);
                mInputManger.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                mTagsCompletionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  mMainContainer.setVisibility(View.VISIBLE);
                        if (mTagsCompletionView.getObjects().size() > 2) {
                            mInputManger.hideSoftInputFromWindow(mTagsCompletionView.getWindowToken(), 0);
                        } else {
                            mInputManger.showSoftInput(mTagsCompletionView, 0);
                            mTagsCompletionView.setCursorVisible(true);
                        }
                    }
                });
            }

            @Override
            public void onTokenAdded(Object token) {
                if (((FoodTag) token).isRest()) {
                    TokenViewUtils.restTokenAdded(token, mTagsCompletionView, mInputManger);
                } else {
                    TokenViewUtils.foodTokenAdded(token, mTagsCompletionView, mInputManger);
                }
                List<Object> tokens = mTagsCompletionView.getObjects();
                TokenViewUtils.refreshRest(tokens, restTotal,
                        restAdapterList, restAdapter);
            }


            @Override
            public void onTokenRemoved(Object token) {
                List<Object> tokens = mTagsCompletionView.getObjects();
                TokenViewUtils.refreshRest(tokens, restTotal,
                        restAdapterList, restAdapter);
            }


}
