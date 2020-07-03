package com.codepath.apps.erastustweet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.erastustweet.models.User;

import org.parceler.Parcels;

import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    private ImageView mBannerImageView;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mUser = Parcels.unwrap(getIntent().getParcelableExtra(UserActivity.class.getSimpleName()));
        mBannerImageView = findViewById(R.id.iv_banner);
        Glide.with(this).load(mUser.bannerUrl).into(mBannerImageView);

        Toolbar toolbarUser = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbarUser);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}