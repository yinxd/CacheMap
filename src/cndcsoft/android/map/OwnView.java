package cndcsoft.android.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class OwnView extends View {

	public OwnView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public OwnView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);// 重写onDraw方法
		// DisplayMetrics dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		// float width=dm.widthPixels;//屏幕宽度
		// float height=dm.heightPixels;//屏幕高度
		canvas.drawColor(Color.BLACK);// 设置背景颜色
		// 坐标轴绘图格式设定
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);// 去锯齿
		paint.setColor(Color.RED);// 颜色

		// 设置K线图坐标轴
		canvas.drawLine(6, 20, 6, 280, paint);
		canvas.drawLine(6, 20, 315, 20, paint);
		canvas.drawLine(315, 20, 315, 280, paint);
		canvas.drawLine(6, 280, 315, 280, paint);
		canvas.drawLine(6, 85, 315, 85, paint);
		canvas.drawLine(6, 150, 315, 150, paint);
		canvas.drawLine(6, 215, 315, 215, paint);

		// 设置交易量柱形图坐标轴
		canvas.drawLine(6, 300, 315, 300, paint);
		canvas.drawLine(6, 355, 315, 355, paint);
		canvas.drawLine(6, 410, 315, 410, paint);
		canvas.drawLine(6, 300, 6, 410, paint);
		canvas.drawLine(315, 300, 315, 410, paint);
		// 坐标轴文字绘图格式设定
		Paint paint1 = new Paint();
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setAntiAlias(true);// 去锯齿
		paint1.setColor(Color.CYAN);// 颜色
		// 设置Y轴文字
		paint1.setTextSize(15);
		canvas.drawText("985.7", 260, 80, paint1);
		canvas.drawText("934.52", 265, 145, paint1);
		canvas.drawText("883.67", 265, 210, paint1);
		canvas.drawText("832.21", 265, 275, paint1);
		// 5日均线格式设定
		Paint paint2 = new Paint();
		paint2.setStyle(Paint.Style.STROKE);
		paint2.setAntiAlias(true);// 去锯齿
		paint2.setColor(Color.WHITE);// 颜色
		// 10日均线格式设定
		Paint paint3 = new Paint();
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setAntiAlias(true);// 去锯齿
		paint3.setColor(Color.YELLOW);// 颜色
		// 20日均线格式设定
		Paint paint4 = new Paint();
		paint4.setStyle(Paint.Style.STROKE);
		paint4.setAntiAlias(true);// 去锯齿
		paint4.setColor(Color.MAGENTA);// 颜色
		// 设置均线文字
		canvas.drawText("MA5:1009.76", 6, 15, paint2);
		canvas.drawText("MA10:1011.72", 81, 15, paint3);
		canvas.drawText("MA20:1000.27", 161, 15, paint4);
		// 设置交易量文字
		canvas.drawText("V:161万手", 6, 295, paint);
		canvas.drawText("MAVOL:187万手", 71, 295, paint2);
		canvas.drawText("MAVOL10:192万手	", 171, 295, paint3);
		// 设置日期文字
		canvas.drawText("20101018", 6, 425, paint);
		canvas.drawText("20101118", 260, 425, paint);
		// 画K线和交易量柱状图
		for (int i = 0; i <= 35; i++) {
			draw_k(i, canvas);
		}
	}

	// 生成随机数
	public float random_num(float low, float high) {
		float rand = (float) Math.random();
		rand = low + (high - low) * rand;
		return rand;
	}

	// 根据解析出的数据画某一天的K线图
	// 参数：n-天数（横坐标），highest-当日最高，lowest-当日最低，begin_high-开盘最高，begin_low-开盘最低
	// 返回当天收盘最低
	public void draw_k(int n, Canvas canvas) {
		float highest, lowest, begin_high, end_low;
		float x;
		float x1, x2, y1, y2, y3, y4;
		float high, low;
		int color;
		int color1;
		lowest = random_num(20, 280);
		highest = random_num(20, lowest);
		begin_high = random_num(lowest, highest);
		end_low = random_num(lowest, highest);
		high = random_num(300, 410);
		low = random_num(300, 410);
		x = 10 + n * 7;// 该条K线真实横坐标
		x1 = (float) (x - 3);// 开收盘矩形区域左侧横坐标
		x2 = (float) (x + 3);// 开收盘矩形区域右侧横坐标
		y1 = lowest;// 当日最低纵坐标
		y2 = end_low;// 开盘最低纵坐标
		y3 = begin_high;// 开盘最高纵坐标
		y4 = highest;// 当日最高纵坐标
		// 设置K线图颜色，若开盘最高价大于等于开盘最低价，则为红色
		if (begin_high > end_low)
			color = Color.RED;
		else
			color = Color.GREEN;
		// 设置交易量柱形图颜色
		if (high > low)
			color1 = Color.RED;
		else
			color1 = Color.GREEN;
		// 设置上下影线的paint
		Paint paint1 = new Paint();
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setAntiAlias(true);// 去锯齿
		paint1.setColor(color);// 颜色

		// 设置柱状体的paint
		Paint paint2 = new Paint();
		paint2.setStyle(Paint.Style.FILL);
		paint2.setAntiAlias(true);// 去锯齿
		paint2.setColor(color);

		// 画上影线
		canvas.drawLine(x, y3, x, y4, paint1);

		// 画下影线
		canvas.drawLine(x, y1, x, y2, paint1);
		// 画k线图柱状体
		if (begin_high != end_low) {
			Path mPath = new Path();
			mPath.moveTo(x1, y3);
			mPath.lineTo(x2, y3);
			mPath.lineTo(x2, y2);
			mPath.lineTo(x1, y2);
			mPath.close();
			canvas.drawPath(mPath, paint2);
		} else
			canvas.drawLine(x1, y2, x2, y3, paint1);
		// 画交易量柱形图
		// 设置交易量格式
		Paint paint3 = new Paint();
		paint3.setStyle(Paint.Style.FILL);
		paint3.setAntiAlias(true);// 去锯齿
		paint3.setColor(color1);

		Path mPath = new Path();
		mPath.moveTo(x1, high);
		mPath.lineTo(x2, high);
		mPath.lineTo(x2, 410);
		mPath.lineTo(x1, 410);
		mPath.close();
		canvas.drawPath(mPath, paint3);

	}

}
