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
		super.onDraw(canvas);// ��дonDraw����
		// DisplayMetrics dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		// float width=dm.widthPixels;//��Ļ���
		// float height=dm.heightPixels;//��Ļ�߶�
		canvas.drawColor(Color.BLACK);// ���ñ�����ɫ
		// �������ͼ��ʽ�趨
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);// ȥ���
		paint.setColor(Color.RED);// ��ɫ

		// ����K��ͼ������
		canvas.drawLine(6, 20, 6, 280, paint);
		canvas.drawLine(6, 20, 315, 20, paint);
		canvas.drawLine(315, 20, 315, 280, paint);
		canvas.drawLine(6, 280, 315, 280, paint);
		canvas.drawLine(6, 85, 315, 85, paint);
		canvas.drawLine(6, 150, 315, 150, paint);
		canvas.drawLine(6, 215, 315, 215, paint);

		// ���ý���������ͼ������
		canvas.drawLine(6, 300, 315, 300, paint);
		canvas.drawLine(6, 355, 315, 355, paint);
		canvas.drawLine(6, 410, 315, 410, paint);
		canvas.drawLine(6, 300, 6, 410, paint);
		canvas.drawLine(315, 300, 315, 410, paint);
		// ���������ֻ�ͼ��ʽ�趨
		Paint paint1 = new Paint();
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setAntiAlias(true);// ȥ���
		paint1.setColor(Color.CYAN);// ��ɫ
		// ����Y������
		paint1.setTextSize(15);
		canvas.drawText("985.7", 260, 80, paint1);
		canvas.drawText("934.52", 265, 145, paint1);
		canvas.drawText("883.67", 265, 210, paint1);
		canvas.drawText("832.21", 265, 275, paint1);
		// 5�վ��߸�ʽ�趨
		Paint paint2 = new Paint();
		paint2.setStyle(Paint.Style.STROKE);
		paint2.setAntiAlias(true);// ȥ���
		paint2.setColor(Color.WHITE);// ��ɫ
		// 10�վ��߸�ʽ�趨
		Paint paint3 = new Paint();
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setAntiAlias(true);// ȥ���
		paint3.setColor(Color.YELLOW);// ��ɫ
		// 20�վ��߸�ʽ�趨
		Paint paint4 = new Paint();
		paint4.setStyle(Paint.Style.STROKE);
		paint4.setAntiAlias(true);// ȥ���
		paint4.setColor(Color.MAGENTA);// ��ɫ
		// ���þ�������
		canvas.drawText("MA5:1009.76", 6, 15, paint2);
		canvas.drawText("MA10:1011.72", 81, 15, paint3);
		canvas.drawText("MA20:1000.27", 161, 15, paint4);
		// ���ý���������
		canvas.drawText("V:161����", 6, 295, paint);
		canvas.drawText("MAVOL:187����", 71, 295, paint2);
		canvas.drawText("MAVOL10:192����	", 171, 295, paint3);
		// ������������
		canvas.drawText("20101018", 6, 425, paint);
		canvas.drawText("20101118", 260, 425, paint);
		// ��K�ߺͽ�������״ͼ
		for (int i = 0; i <= 35; i++) {
			draw_k(i, canvas);
		}
	}

	// ���������
	public float random_num(float low, float high) {
		float rand = (float) Math.random();
		rand = low + (high - low) * rand;
		return rand;
	}

	// ���ݽ����������ݻ�ĳһ���K��ͼ
	// ������n-�����������꣩��highest-������ߣ�lowest-������ͣ�begin_high-������ߣ�begin_low-�������
	// ���ص����������
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
		x = 10 + n * 7;// ����K����ʵ������
		x1 = (float) (x - 3);// �����̾���������������
		x2 = (float) (x + 3);// �����̾��������Ҳ������
		y1 = lowest;// �������������
		y2 = end_low;// �������������
		y3 = begin_high;// �������������
		y4 = highest;// �������������
		// ����K��ͼ��ɫ����������߼۴��ڵ��ڿ�����ͼۣ���Ϊ��ɫ
		if (begin_high > end_low)
			color = Color.RED;
		else
			color = Color.GREEN;
		// ���ý���������ͼ��ɫ
		if (high > low)
			color1 = Color.RED;
		else
			color1 = Color.GREEN;
		// ��������Ӱ�ߵ�paint
		Paint paint1 = new Paint();
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setAntiAlias(true);// ȥ���
		paint1.setColor(color);// ��ɫ

		// ������״���paint
		Paint paint2 = new Paint();
		paint2.setStyle(Paint.Style.FILL);
		paint2.setAntiAlias(true);// ȥ���
		paint2.setColor(color);

		// ����Ӱ��
		canvas.drawLine(x, y3, x, y4, paint1);

		// ����Ӱ��
		canvas.drawLine(x, y1, x, y2, paint1);
		// ��k��ͼ��״��
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
		// ������������ͼ
		// ���ý�������ʽ
		Paint paint3 = new Paint();
		paint3.setStyle(Paint.Style.FILL);
		paint3.setAntiAlias(true);// ȥ���
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
