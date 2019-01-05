package com.lyz.ble.view;import android.content.Context;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.Paint;import android.widget.ImageView;/** * lamp *  *  */public class IconImageView extends ImageView {	private Bitmap mBitmap;	private Bitmap mBitmap2;	Paint p;	//private Context context;	public IconImageView(Context context,int image) {		super(context);		// TODO Auto-generated constructor stub		this.setBackgroundResource(image);		//this.context = context;		mBitmap = BitmapFactory.decodeStream(context.getResources()				.openRawResource(image));		mBitmap2 = mBitmap;	}	/**	 * 设置	 * 	 * @param color	 */	public void setColor(int color) {		p = new Paint();		p.setColor(color);		mBitmap2 = mBitmap.extractAlpha();		invalidate();	}	@Override	protected void onDraw(Canvas canvas) {		canvas.drawColor(Color.TRANSPARENT);		int width = (this.getWidth() - mBitmap2.getWidth()) / 2;		int height = (this.getHeight() - mBitmap2.getHeight()) / 2;		canvas.drawBitmap(mBitmap2, width, height, p);			}}