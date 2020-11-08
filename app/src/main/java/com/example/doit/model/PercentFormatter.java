package com.example.doit.model;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class PercentFormatter extends ValueFormatter implements IValueFormatter {

    protected DecimalFormat mFormat;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PercentFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");

    }

    /**
     * Allow a custom decimalformat
     *
     * @param format
     */
    public PercentFormatter(DecimalFormat format) {
        this.mFormat = format;
    }

    public int getDecimalDigits() {
        return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " %";
    }
}