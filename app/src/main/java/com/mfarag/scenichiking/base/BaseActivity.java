package com.mfarag.scenichiking.base;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        ButterKnife.bind(this);

        initializeObjects();
        initializeViews();
    }

    /**
     * returns the screen layout
     *
     * @return
     */
    protected abstract int getLayout();

    /**
     * initializeObjects method initializes the screen different objects.
     */
    protected abstract void initializeObjects();

    /**
     * initializeViews method initializes the screen view, sets the
     * views listeners and setup all necessary texts for the screen.
     */
    protected abstract void initializeViews();
}