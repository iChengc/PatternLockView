/*
 * Copyright (C) 2016 iChengc
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
package com.cc.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChengC on 2016/3/24.
 */
public class PatternView extends View {

    private static final int DEFAULT_CELL_BASE_COUNT = 3;

    /**
     * The color of the cell;
     */
    private int mCellColor;

    /**
     * The error color
     */
    private int mErrorColor;

    /**
     * The padding
     */
    private int mPadding;

    /**
     * The offset that layout the cells align the center horizontal of the view.
     */
    private int mCenterOffset;

    /**
     * The spacing of the two cell
     */
    private int mSpacing;

    /**
     * The radius of the cell
     */
    private int mCellRadius;

    private int mStrokeWidth;

    /**
     * The base count of the cell, the real count of cell is {@code mCellBaseCount} * {@code mCellBaseCount}
     */
    private int mCellBaseCount;
    private boolean mIsError = false;

    /**
     * The selected cells
     */
    private List<Integer> mSelectedCells;

    private boolean mIsShowPath;

    /**
     * Specify whether is setup the gesture pass code..
     */
    private boolean mIsSetup;

    /**
     * The cell selected status.
     */
    private boolean[] mCellStatus;

    private boolean mIsShowingResult = false;

    private OnFinishListener mFinishListener;

    private CellDrawable mCellDrawable;
    private Paint mPaint;

    private Point mLastPoint;
    private Point mTouchPoint;
    private Point mNextPoint;

    /**
     * The interface definition for callback when finish select
     */
    public interface OnFinishListener {
        /**
         * The callback method when finish selected.
         *
         * @param patternView    the patternView
         * @param result         the select result as integer list. e.g:"0,1,2,3,4,5,6,7,8,9"
         * @param resultAsString the selected result as String. the string format is like:"0-1-2-3-4-5-6-7-8-9".
         * @return true if the result is correct otherwise false.
         */
        boolean onFinish(PatternView patternView, List<Integer> result, String resultAsString);
    }

    public PatternView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PatternView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mCellDrawable = new CellDrawable();
        mSelectedCells = new ArrayList<Integer>();
        mNextPoint = new Point();
        mLastPoint = new Point();
        mErrorColor = context.getResources().getColor(android.R.color.holo_red_light);
        mPadding = getPaddingTop();
        mStrokeWidth = dip2px(context, 2f);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.PatternView);
        mIsShowPath = typedArray.getBoolean(R.styleable.PatternView_showPath, true);
        mCellBaseCount = typedArray.getInt(R.styleable.PatternView_cellBaseCount, DEFAULT_CELL_BASE_COUNT);
        mCellColor = typedArray.getColor(R.styleable.PatternView_cellColor, context.getResources().getColor(android.R.color.white));
        mSpacing = typedArray.getDimensionPixelOffset(R.styleable.PatternView_cellSpacing, dip2px(context, 32));
        mCellRadius = typedArray.getDimensionPixelOffset(R.styleable.PatternView_cellRadius, -1);
        mCellStatus = new boolean[mCellBaseCount * mCellBaseCount];

        typedArray.recycle();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.cellStatus = mCellStatus;
        ss.selectedCells = mSelectedCells;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mSelectedCells = ss.selectedCells;
        mCellStatus = ss.cellStatus;
    }

    public void setOnFinishListener(OnFinishListener listener) {
        mFinishListener = listener;
    }

    /**
     * Set base cell count. the cell counts is {@code count} * {@code count}.
     *
     * @param count the base cell count.
     */
    public void setCellBaseCount(int count) {
        mCellBaseCount = count;
        mCellStatus = new boolean[mCellBaseCount * mCellBaseCount];

        // If set the cell base count programmer need to re-calculate the cell's radius.
        mCellRadius = -1;
        calculateCellRadius();
        invalidate();
    }

    public boolean isSetup() {
        return mIsSetup;
    }

    public void setIsSetup(boolean isSetup) {
        this.mIsSetup = isSetup;
    }

    public boolean isShowPath() {
        return mIsShowPath;
    }

    public void setIsShowPath(boolean isShowPath) {
        this.mIsShowPath = isShowPath;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateCellRadius();
    }

    private void calculateCellRadius() {
        if (mCellBaseCount <= 0) {
            return;
        }

        final int size = Math.min(getMeasuredHeight(), getMeasuredWidth());
        if (mCellRadius == -1) {
            int remainSpace = size - (mPadding << 1) - mSpacing * (mCellBaseCount - 1);
            if (remainSpace <= 0) {
                throw new RuntimeException("No enough space for drawing the cells");
            }
            mCellRadius = remainSpace / (mCellBaseCount << 1);
            mCenterOffset = size == getMeasuredHeight() ? ((getMeasuredWidth() - getMeasuredHeight()) >> 1) : 0;
        } else {
            final int cellsUsedSpace = (mCellRadius << 1) * mCellBaseCount;
            if (cellsUsedSpace > size - (mPadding << 1)) {
                throw new RuntimeException("No enough space for drawing the cells");
            }

            final int remainSpacing = size - cellsUsedSpace - (mPadding << 1);
            if (remainSpacing - mSpacing * (mCellBaseCount - 1) < 0) {
                mSpacing = remainSpacing / (mCellBaseCount - 1);
            }

            mCenterOffset = ((getMeasuredWidth()  - cellsUsedSpace - mSpacing * (mCellBaseCount - 1)) >> 1) - mPadding;
        }
        mCellDrawable.init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnselectedCells(canvas);
        drawSelectedCells(canvas);
    }

    private void drawUnselectedCells(Canvas canvas) {
        for (int i = 0; i < mCellStatus.length; i++) {
            if (!mCellStatus[i]) {
                calcPositionOfCell(i, mNextPoint);
                mCellDrawable.set(mNextPoint.x, mNextPoint.y, false, CellDrawable.ANGEL_NONE);
                mCellDrawable.draw(canvas);
            }
        }
    }

    private void drawSelectedCells(Canvas canvas) {
        for (int i = 0; i < mSelectedCells.size(); i++) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setColor(mIsError ? mErrorColor : mCellColor);
            calcPositionOfCell(mSelectedCells.get(i), mLastPoint);
            double angle = CellDrawable.ANGEL_NONE;
            if (i < mSelectedCells.size() - 1) {
                calcPositionOfCell(mSelectedCells.get(i + 1), mNextPoint);
                angle = getAngle(mSelectedCells.get(i + 1));
                if (mIsShowPath || mIsSetup) {
                    canvas.drawLine(mLastPoint.x, mLastPoint.y, mNextPoint.x, mNextPoint.y, mPaint);
                }
            } else {
                if (mTouchPoint != null && (mIsShowPath || mIsSetup)) {
                    canvas.drawLine(mLastPoint.x, mLastPoint.y, mTouchPoint.x, mTouchPoint.y, mPaint);
                }
            }

            mCellDrawable.set(mLastPoint.x, mLastPoint.y, true, angle);
            mCellDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (mIsShowingResult) {
                return false;
            }

            reset();
        }

        final float x = event.getX();
        final float y = event.getY();
        final int touchCellIndex = getCellIndex(x, y);
        if (touchCellIndex != -1) {
            if (!mCellStatus[touchCellIndex]) {
                mCellStatus[touchCellIndex] = true;
                mSelectedCells.add(touchCellIndex);
                if (mTouchPoint == null) {
                    mTouchPoint = new Point();
                }
                mTouchPoint.set((int) x, (int) y);
            }
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (mTouchPoint != null) {
                    mTouchPoint.set((int) x, (int) y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                finish();
                break;
        }
        return true;
    }

    private void reset() {
        mIsError = false;
        mSelectedCells.clear();
        mTouchPoint = null;
        Arrays.fill(mCellStatus, false);
        mIsShowingResult = false;
    }

    private void finish() {
        mIsShowingResult = true;
        if (mFinishListener != null && mTouchPoint != null) {
            if (!mFinishListener.onFinish(this, mSelectedCells, getPasswordAsString(mSelectedCells))) {
                mIsError = true;
                invalidate();
                resetViewAfterDelay(1000);
            } else {
                resetViewAfterDelay(1000);
            }
        } else {
            resetViewAfterDelay(1000);
        }
    }

    private void resetViewAfterDelay(long delay) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
                invalidate();
            }
        }, delay);
    }

    private double getAngle(int nextIndex) {
        if (!mIsSetup && !mIsError) {
            return CellDrawable.ANGEL_NONE;
        }

        calcPositionOfCell(nextIndex, mNextPoint);
        final int deltaY = mNextPoint.y - mLastPoint.y;
        final int deltaX = mNextPoint.x - mLastPoint.x;
        if (deltaX == 0 && deltaY == 0) {
            return 0;
        }

        double offset = 0;
        if (deltaX == 0) {
            return (deltaY > 0 ? Math.PI : -Math.PI) / 2;
        } else if (deltaY == 0) {
            return deltaX > 0 ? 0 : Math.PI;
        } else if (deltaX < 0 && deltaY > 0) {
            offset = -Math.PI;
        } else if (deltaX < 0 && deltaY < 0) {
            offset = Math.PI;
        }

        return Math.atan((double) deltaY / deltaX) + offset;
    }

    /**
     * Get index of the cell that located in the specified position.
     *
     * @param xPosition the x coordinate position
     * @param yPosition the y coordinate position
     * @return -1 if no cell located in this position otherwise return the index of the cell.
     */
    private int getCellIndex(float xPosition, float yPosition) {
        if (xPosition < mPadding + mCenterOffset || xPosition > getWidth() - mPadding - mCenterOffset
                || yPosition < mPadding || yPosition > getHeight() - mPadding) {
            return -1;
        }

        int yOffset = ((int) yPosition - mPadding) / ((mCellRadius << 1) + mSpacing);
        if (yOffset >= mCellBaseCount || ((int) yPosition - mPadding) % ((mCellRadius << 1) + mSpacing) > (mCellRadius << 1)) {
            return -1;
        }

        int xOffset = ((int) xPosition - mPadding - mCenterOffset) / ((mCellRadius << 1) + mSpacing);
        if (xOffset >= mCellBaseCount || ((int) xPosition - mPadding - mCenterOffset) % ((mCellRadius << 1) + mSpacing) > (mCellRadius << 1)) {
            return -1;
        }
        return yOffset * mCellBaseCount + xOffset;
    }

    /**
     * get original point of the cell.
     *
     * @param index the index of the cell
     * @param point the point that hold the position of the cell
     * @return the original point of the cell.
     */
    private void calcPositionOfCell(int index, Point point) {
        if (index < 0 || index >= mCellStatus.length) {
            return;
        }

        final int row = index / mCellBaseCount;
        final int column = index % mCellBaseCount;

        point.set(mPadding + mCenterOffset + column * ((mCellRadius << 1) + mSpacing) + mCellRadius, mPadding + row * ((mCellRadius << 1) + mSpacing) + mCellRadius);
    }

    private String getPasswordAsString(List<Integer> selectedCells) {
        if (selectedCells == null || selectedCells.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedCells.size() - 1; i++) {
            sb.append(selectedCells.get(i));
            sb.append('-');
        }
        sb.append(selectedCells.get(selectedCells.size() - 1));

        return sb.toString();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState {
        List<Integer> selectedCells;
        boolean[] cellStatus;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(selectedCells);
            out.writeBooleanArray(cellStatus);
        }

        @SuppressWarnings("hiding")
        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            selectedCells = new ArrayList<>();
            in.readList(selectedCells, Integer.class.getClassLoader());
            cellStatus = in.createBooleanArray();
        }
    }

    private class CellDrawable extends Drawable {
        static final double ANGEL_NONE = Double.MAX_VALUE;

        private float mPositionX, mPositionY;
        private double mAngle;
        private boolean mIsSelected;

        private int mInnerCircleRadius;

        private Path mTrianglePath;

        public CellDrawable() {
            mTrianglePath = new Path();
            mAngle = ANGEL_NONE;
        }

        void init() {
            mInnerCircleRadius = mCellRadius / 3;
        }

        void set(float x, float y, boolean isSelected, double angle) {
            mPositionX = x;
            mPositionY = y;
            mIsSelected = isSelected;
            mAngle = angle;
        }

        @Override
        public void draw(Canvas canvas) {
            mPaint.setColor(mIsError && mIsSelected ? mErrorColor : mCellColor);
            // Draw inner circle
            if (mIsSelected) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mPositionX, mPositionY, mInnerCircleRadius, mPaint);
            }
            drawCircle(canvas);
            drawTriangle(canvas);
        }

        private void drawCircle(Canvas canvas) {
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mPositionX, mPositionY, mCellRadius, mPaint);
        }

        private void drawTriangle(Canvas canvas) {
            if (mAngle == ANGEL_NONE || mCellRadius - mInnerCircleRadius <= mStrokeWidth) {
                return;
            }

            mPaint.setStyle(Paint.Style.FILL);

            // The height of the triangle
            int triangleHeight = (mCellRadius - mStrokeWidth - mInnerCircleRadius) / 3;
            if (triangleHeight < mStrokeWidth) return;
            // The distance between the top vertex of the triangle and the cell's original point.
            int distanceTopVertex = (triangleHeight << 1) + mInnerCircleRadius;

            final double bottomVertexAngle = Math.atan(triangleHeight / Math.sqrt(3) / (distanceTopVertex - triangleHeight));
            final double distanceBottomVertex = Math.sqrt(Math.pow(triangleHeight, 2) / 3 + Math.pow(distanceTopVertex - triangleHeight, 2));


            final float vertexX = mPositionX + distanceTopVertex * (float) Math.cos(mAngle);
            final float vertexY = mPositionY + distanceTopVertex * (float) Math.sin(mAngle);
            final float vertexX1 = mPositionX + (float) (distanceBottomVertex * Math.cos(mAngle + bottomVertexAngle));
            final float vertexY1 = mPositionY + (float) (distanceBottomVertex * Math.sin(mAngle + bottomVertexAngle));
            final float vertexX2 = mPositionX + (float) (distanceBottomVertex * Math.cos(mAngle - bottomVertexAngle));
            final float vertexY2 = mPositionY + (float) (distanceBottomVertex * Math.sin(mAngle - bottomVertexAngle));

            mTrianglePath.reset();
            mTrianglePath.moveTo(vertexX, vertexY);
            mTrianglePath.lineTo(vertexX1, vertexY1);
            mTrianglePath.lineTo(vertexX2, vertexY2);
            mTrianglePath.close();
            canvas.drawPath(mTrianglePath, mPaint);
        }

        @Override
        public void setAlpha(int i) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}
