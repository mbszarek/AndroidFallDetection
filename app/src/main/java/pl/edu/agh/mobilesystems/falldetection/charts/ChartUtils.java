package pl.edu.agh.mobilesystems.falldetection.charts;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class ChartUtils {

    private static int lastIndex = 0;
    private static final int MAX_VALUES = 50;

    public static void addEntry(LineChart chart, double xCoordinate, double yCoordinate, double zCoordinate, double distance) {
        LineData data = chart.getData();
        if (data == null) {
            chart.setData(new LineData());
            data = chart.getData();
        }

        LineDataSet xDataSet = (LineDataSet) data.getDataSetByIndex(0);
        LineDataSet yDataSet = (LineDataSet) data.getDataSetByIndex(1);
        LineDataSet zDataSet = (LineDataSet) data.getDataSetByIndex(2);
        LineDataSet distanceDataSet = (LineDataSet) data.getDataSetByIndex(3);

        if (xDataSet == null) {
            xDataSet = new LineDataSet(null, "X");
            xDataSet.setValueTextColor(Color.TRANSPARENT);
            xDataSet.setColor(Color.RED);
            xDataSet.setHighlightEnabled(false);
            data.addDataSet(xDataSet);
        }
        if (yDataSet == null) {
            yDataSet = new LineDataSet(null, "Y");
            yDataSet.setValueTextColor(Color.TRANSPARENT);
            yDataSet.setColor(Color.GREEN);
            yDataSet.setHighlightEnabled(false);
            data.addDataSet(yDataSet);
        }
        if (zDataSet == null) {
            zDataSet = new LineDataSet(null, "Z");
            zDataSet.setValueTextColor(Color.TRANSPARENT);
            zDataSet.setColor(Color.BLUE);
            zDataSet.setHighlightEnabled(false);
            data.addDataSet(zDataSet);
        }
        if (distanceDataSet == null) {
            distanceDataSet = new LineDataSet(null, "Distance");
            distanceDataSet.setValueTextColor(Color.TRANSPARENT);
            distanceDataSet.setColor(Color.BLACK);
            distanceDataSet.setLineWidth(5);
            distanceDataSet.setHighlightEnabled(false);
            data.addDataSet(distanceDataSet);
        }

        xDataSet.addEntry(new Entry(lastIndex, (float) xCoordinate));
        yDataSet.addEntry(new Entry(lastIndex, (float) yCoordinate));
        zDataSet.addEntry(new Entry(lastIndex, (float) zCoordinate));
        distanceDataSet.addEntry(new Entry(lastIndex, (float) distance));

        lastIndex++;

        if (xDataSet.getEntryCount() > MAX_VALUES) {
            xDataSet.removeFirst();
            yDataSet.removeFirst();
            zDataSet.removeFirst();
            distanceDataSet.removeFirst();
        }


        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(MAX_VALUES);
        chart.moveViewToX(xDataSet.getEntryCount());
        YAxis yLeft = chart.getAxisLeft();
        yLeft.setLabelCount(5);
        yLeft.setAxisMaximum(10);
        yLeft.setAxisMinimum(-10);
        YAxis yRight = chart.getAxisRight();
        yRight.setLabelCount(5);
        yRight.setAxisMaximum(10);
        yRight.setAxisMinimum(-10);
    }
}
