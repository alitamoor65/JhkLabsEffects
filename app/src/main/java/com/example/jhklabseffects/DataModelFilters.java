package com.example.jhklabseffects;

import android.graphics.Bitmap;

public class DataModelFilters {
    Bitmap filterBitmap;
    String filterName;

    public DataModelFilters(Bitmap filterBitmap, String filterName) {
        this.filterBitmap = filterBitmap;
        this.filterName = filterName;
    }

    public Bitmap getFilterBitmap() {
        return filterBitmap;
    }

    public void setFilterBitmap(Bitmap filterBitmap) {
        this.filterBitmap = filterBitmap;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
