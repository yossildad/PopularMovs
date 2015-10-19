package com.eldad.yossi.popularmovs;

import java.util.Date;

/**
 * Created by Tamar on 18/10/2015.
 */
public class MyMovie {

    private String mTitle;
    private String mOverview;
    private Double mRating;
    private String mPosterPath;

    public Date getmReleaseDate() {
        return this.mReleaseDate;
    }

    public void setmReleaseDate(Date mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public void setmRating(Double mRating) {
        this.mRating = mRating;
    }

    private Date mReleaseDate;

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public void setmRating(double mRating) {
        this.mRating = mRating;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }



    public String getmTitle() {
        return this.mTitle;
    }

    public String getmOverview() {
        return this.mOverview;
    }

    public double getmRating() {
        return this.mRating;
    }

    public String getmPosterPath() {
        return this.mPosterPath;
    }


}
