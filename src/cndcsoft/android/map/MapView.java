package cndcsoft.android.map;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cndcsoft.android.map.demo.MapShow;
import cndcsoft.android.map.demo.R;
import cndcsoft.android.map.lib.IMapService;
import cndcsoft.android.map.lib.MapImage;
import cndcsoft.android.map.lib.MapParam;
import cndcsoft.android.map.lib.MapResult;
import cndcsoft.android.map.lib.MapServiceFactory;
import cndcsoft.android.map.lib.Point;
import cndcsoft.android.map.lib.MapServiceFactory.MapServieType;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

public class MapView extends View implements OnTouchListener, OnGestureListener {

	public Bitmap _backgroundimage = null;// ����ͼƬ
	public int controlwidth;
	public int controlheight;

	private HashMap<String, MapImage> getList = new HashMap<String, MapImage>();
	private IMapService mapService = null;
	private MapParam mapParameter = null;
	private MapResult _mapResult = null;
	private MapImage img = null;
	private boolean ismousedown = false;
	private int pre_mouse_x, pre_mouse_y;
	private boolean ispaned;
	private int clientcx, clientcy;
	private ActionType _action = ActionType.Pan;
	private boolean _startPointerOnce = false;
	private int _zoomspeedalfa = 2;
	private boolean _ishowpointer = false;// ��Ϣ��
	private Point _pPointerPos;
	// ˫�������
	private Bitmap bitImage = null;
	private Bitmap bitBuffer = null;
	private Paint mPaint = null;
	// �̹߳���
	public static final int REFRESH = 0x000001;
	private Handler mHandler = null;
	private Handler messageHandler = null;
	// ����
	private GestureDetector mGestureDetector;
	private Scroller scroller;

	// ����������ٶȣ���С���벻��Ӧ
	private static final int FLING_MIN_DISTANCE = 2;
	private static final int FLING_MIN_VELOCITY = 2;

	private Context fcontext;

	// ���캯��
	public MapView(Context context) {
		super(context);

	}

	// ���캯����
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		fcontext = context;

		// ��Ļ��С
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int Width = dm.widthPixels;
		int Height = dm.heightPixels;
		controlwidth = Width;
		controlheight = Height;

		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);// ȥ���

		// ������򿪣��޷��յ���ʱ��������ô�����жϾ��޴ӽ���
		mGestureDetector = new GestureDetector(this);// ����
		this.setLongClickable(true);
	}

	private Bitmap createBuffer() {
		if (bitBuffer != null && !bitBuffer.isRecycled()) {
			bitBuffer.recycle();
			bitBuffer = null;
		}
		bitBuffer = Bitmap.createBitmap(controlwidth, controlheight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitBuffer);
		Paint myPaint = new Paint();
		myPaint.setColor(Color.WHITE);
		// ����
		Bitmap _backgroundimage = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg);
		if (_backgroundimage != null) {

			// ���Ʊ���ͼ��Ƭ
			int w = _backgroundimage.getWidth();
			int h = _backgroundimage.getHeight();
			int cw = bitBuffer.getWidth();
			int ch = bitBuffer.getHeight();
			int x = 0;
			while (x < cw) {
				int y = 0;
				while (y < ch) {
					canvas.drawBitmap(_backgroundimage, x, y, myPaint);
					y += h;
				}
				x += w;
			}
		} else {
			canvas.drawColor(Color.WHITE);
		}

		canvas = null;
		myPaint = null;
		return bitBuffer;
	}

	// ��ȡSD��·��
	public static String getExternalStoragePath() {
		// ��ȡSdCard״̬
		String state = android.os.Environment.getExternalStorageState();
		// �ж�SdCard�Ƿ���ڲ����ǿ��õ�
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return android.os.Environment.getExternalStorageDirectory()
						.getPath();
			}
		}
		return null;
	}

	// ��ȡ�洢����ʣ����������λΪ�ֽ�
	public static long getAvailableStore(String filePath) {
		// ȡ��sdcard�ļ�·��
		StatFs statFs = new StatFs(filePath);
		// ��ȡblock��SIZE
		long blocSize = statFs.getBlockSize();
		// ��ȡBLOCK����
		// long totalBlocks = statFs.getBlockCount();
		// ��ʹ�õ�Block������
		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}

	// �������
	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (this) {
			canvas.drawColor(Color.WHITE);// ����
			bitImage = createBuffer();
			Canvas mCanvas = new Canvas(bitImage);
			if (_mapResult == null)
				return;
			// ��ʼ���Ʊ�����ͼ��Ƭ
			for (MapImage mi : _mapResult.MapImages.values()) {
				if (mi.Image == null)
					continue;
				// Rect destRect = new Rect(mi.left, mi.top,
				// mi.Image.getWidth(),
				// mi.Image.getHeight());
				// Rect srcRect = new Rect(0, 0, mi.Image.getWidth(), mi.Image
				// .getHeight());
				if (mi.Image.isRecycled()) {
					// <--����ĵط���
					Log.v("001", "this image object is aleadly recycled!!!"
							+ "----" + mi.id);
					continue;
				}
				// mCanvas.drawBitmap(mi.Image, destRect,srcRect ,
				// mPaint);//�������ֻ���������Ͻ�һС�����򡣡�
				mCanvas.drawBitmap(mi.Image, mi.left, mi.top, mPaint);
			}
			canvas.drawBitmap(bitImage, canvas.getClipBounds(), canvas
					.getClipBounds(), mPaint);
			// canvas.drawBitmap(bitImage, 0, 0, mPaint);
			// ��ʼ���ƿ���ͼ����Ƭ(����googlemap��λЧ��)
			if (_ishowpointer) {
				if (_pPointerPos != null) {
					Paint mmPaint = new Paint();
					mmPaint.setStyle(Style.FILL);
					mmPaint.setColor(Color.RED);
					mmPaint.setAntiAlias(true);// ȥ���
					Paint mmmPaint = new Paint();
					mmmPaint.setColor(Color.BLUE);
					mmmPaint.setAntiAlias(true);
					mmmPaint.setStyle(Style.STROKE);// ����
					mmmPaint.setStrokeWidth(3);// �����
					
					int[] ret = new int[2];
					ret = mapParameter.MapToClient(_pPointerPos.X, _pPointerPos.Y);
					
					canvas.drawCircle((float)ret[0],
							(float)ret[1], 5, mmPaint);
					canvas.drawCircle((float) ret[0],
							(float)ret[1], 75, mmmPaint);
					mmPaint = null;
					mmmPaint = null;
				}
			}
			mCanvas = null;
			if (bitImage.isRecycled()) {
				bitImage.recycle();
			}
			bitImage = null;
		}
		super.onDraw(canvas);
	}

	// private void Draw

	// ���ص�ͼ����
	public void LoadMap(MapServieType serviceType, Object[] paramlist) {
		/*
		 * �������� (Զ��ģʽ) " http://192.168.1.156:8090/MobileGISServer", //Զ����Ƭ�����ַ
		 * 
		 * @"\�洢��\MAP-DATA2", //���ر���·�� "yz0802_m" //��ͼ����
		 */
		invalidate();
		// ��ʼ����ͼ����(����orԶ��)
		mapService = MapServiceFactory.getMapService(serviceType);
		if (paramlist.length == 2) {
			// ���س�ʼ��
			// ��������ƬĿ¼/��ͼ����/�ؼ���/�ؼ���
			Object[] params = new Object[] { paramlist[0], paramlist[1],
					controlwidth, controlheight };
			mapService.Initialize(params);

		} else if (paramlist.length == 3) {
			// Զ�̳�ʼ��
			// ��������Ƭ�����ַ/��Ƭ���ػ���·��/��ͼ����/�ؼ���/�ؼ���
			Object[] params = new Object[] { paramlist[0], paramlist[1],
					paramlist[2], controlwidth, controlheight };
			mapService.Initialize(params);
		} else
			return;

		// ��ȡĬ�ϵ�ͼ����
		mapParameter = mapService.getDefaultMapParam();

		if (mapParameter == null) {
			return;
		}

		// ���Ƶ�ͼ
		refrashMap();
		invalidate();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// ���յ���Ϣʱ���Խ�����и���
				MapImage mii = (MapImage) msg.obj;
				setDety(mii);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void setDety() {
		postInvalidate();
	}

	// ˢ�µ�ͼ
	private void setDety(MapImage mi) {
		if (mi == null)
			return;
		// postInvalidate(mi.left, mi.top, mi.size, mi.size);
		Rect rect = new Rect(mi.left, mi.top, mi.size, mi.size);
		Invalidate(rect);
	}

	private void Invalidate(Rect clipBounds) {
		clipBounds.offset(0, 0);// mapviewΪ0
		// postInvalidate(clipBounds.left,clipBounds.top,clipBounds.right,clipBounds.right);
		postInvalidate();
	}

	public void startDrawLayers(boolean started) {
		if (started) {

		}

	}

	// ��ʾ��ǰλ��
	public void ShowLocation(double dx, double dy) {
		double[] ls = getLevels();
		_ishowpointer = true;
		int[] ret = new int[2];
		ret = mapParameter.MapToClient(dx, dy);
		_pPointerPos = new Point(dx, dy);
		if (ls != null) {
			Zoom(ls[ls.length - 1], dx, dy);
		} else
			Zoom(1, ret[0], ret[1]);
	}

	// ��λ
	public void animateTo(Point mGeoPoint) {
		double[] ls = getLevels();
		Zoom(ls[ls.length - 1], mGeoPoint.X, mGeoPoint.Y);
	}

	// / <summary>
	// / ƽ�Ƶ�ͼ
	// / </summary>
	// / <param name="dx">x��ƽ����</param>
	// / <param name="dy">y��ƽ����</param>
	public void Pan(int dx, int dy) {
		if (mapParameter != null) {
			mapParameter.Pan(dx, dy);
			refrashMap();
		}
	}

	// / <summary>
	// / ����
	// / </summary>
	// / <param name="scaleFilter">���ű���</param>
	// / <param name="px">��Ļ�������ĵ�</param>
	// / <param name="py">��Ļ�������ĵ�</param>
	public void Zoom(double scaleFilter, int px, int py) {
		if (mapParameter != null) {
			double ow = mapParameter.ViewBound.getWidth();
			mapParameter.Zoom(scaleFilter, px, py);
			double nw = mapParameter.ViewBound.getWidth();
			// ����Ч��
			zoomflash(ow, nw, px, py);
			Log.v("_____________", String.valueOf(ow)+":"+String.valueOf(nw));
			refrashMap();
		}
	}

	// / <summary>
	// / ����
	// / </summary>
	// / <param name="scale">������</param>
	// / <param name="x">��ͼ�������ĵ�</param>
	// / <param name="y">��ͼ�������ĵ�</param>
	public void Zoom(double scale, double x, double y) {
		if (mapParameter != null) {
			mapParameter.ZoomByScaleCenter(scale, new Point(x, y));
			refrashMap();
		}
	}

	// / <summary>
	// / ִ����չ����
	// / </summary>
	// / <param name="commandName">��չ��������</param>
	// / <param name="paramlist">�������</param>
	// / <returns>����ִ�н��</returns>
	public String ExeExpandCommand(String commandName, Object[] paramlist) {
		return mapService.expandCommand(commandName, paramlist);
	}

	// / <summary>
	// / ��ȡ�������õ�ͼ��ǰ�������
	// / </summary>
	public ActionType getMapAction() {
		return _action;
		// get { return _action; }
		// set { _action = value; }
	}

	// / <summary>
	// / ��ȡ�������õ�ͼ���Ż���������ֵ
	// / </summary>
	public int getZoomSpeedAlfa() {
		return _zoomspeedalfa;
	}

	// / <summary>
	// / ��ȡ��ǰ��ͼ����
	// / </summary>
	public String getMapName() {

		if (mapParameter != null) {
			return mapParameter.MapName;
		}
		return "";
	}

	// / <summary>
	// / ��ȡ�����ͼ�ı����߲㼶������Ƿǻ����ͼ�򷵻�NULL
	// / </summary>
	public double[] getLevels() {
		// return
		// LocalMapParam param =new L
		if (mapService.getLevel() == null)
			return null;
		double[] ret = new double[mapService.getLevel().length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = mapService.getLevel()[i].Scale;
		}
		return ret;
	}

	// / <summary>
	// / ��Ϣ��
	// / </summary>
	public Point getPointerPosition() {
		return _pPointerPos;
	}

	// / <summary>
	// / �Ƿ�������Ϣ��
	// / </summary>
	public boolean getStartPointerOnce() {
		return _startPointerOnce;
	}

	// / <summary>
	// / �Ƿ���ʾ��Ϣ��
	// / </summary>
	public boolean IsShowPointer() {
		return _ishowpointer;
	}

	public Bitmap getbackgroundimage() {
		return _backgroundimage;

	}

	// ���õ�ͼ֧�����ţ����÷Ŵ���Сͼ�갴ť
	public boolean setBuiltInZoomControls(boolean isBuilt) {
		if (isBuilt) {
			return true;
		}
		return false;
	}

	// ��ͨģʽ
	public boolean setTraffic(boolean isshow) {
		if (isshow) {
			return true;
			// code
		}
		return false;
	}

	// ����
	public boolean setSatellite(boolean isshow) {
		if (isshow) {
			return true;
			// code
		}
		return false;
	}

	// �־�
	public boolean setStreetView(boolean isshow) {
		if (isshow) {
			return true;
			// code
		}
		return false;
	}

	/**
	 * ˽�к���
	 */

	// / <summary>
	// / ��С��ť�¼�
	// / </summary>
	void _abZoomOut_Event_MouseUp() {
		Zoom(0.5, clientcx, clientcy);
	}

	// / <summary>
	// / �Ŵ�ť�¼�
	// / </summary>
	void _abZoomIn_Event_MouseUp() {
		Zoom(2.0, clientcx, clientcy);
	}

	/**
	 * ˢ�µ�ͼ
	 */
	private void refrashMap() {

		MapResult mapResult = mapService.getMapImage(mapParameter);
		_mapResult = mapResult;
		for (String newid : mapResult.newIDs) {
			final MapImage mi = mapResult.MapImages.get(newid);
			if (mi.Image == null) {
				// ���µ�ͼƬ�Ļ�ȡ����ӵ��߳���
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 1;
						mi.getData();
						msg.obj = mi;
						//
						handler.sendMessage(msg);

						// �ݶ�50ms�ȴ�UIˢ�£�
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
		}

		// ��Ϣ��
		// if (_ishowpointer)
		// {
		// int[] p = mapParameter.MapToClient(_pPointerPos.X,_pPointerPos.Y);
		// _abPointer.Left = p[0] - 5;
		// _abPointer.Top = p[1] - _abPointer.Height;
		// }

		invalidate();
	}

	// / <summary>
	// / ����Ч��
	// / </summary>
	// / <param name="ow">�ɵ�ͼ�������</param>
	// / <param name="nw">�µ�ͼ�������</param>
	// / <param name="px">��Ļ���ĵ�</param>
	// / <param name="py">��Ļ���ĵ�</param>
	private void zoomflash(double ow, double nw, int px, int py) {
		if (_zoomspeedalfa < 1 || ow == nw)
			return;

		double ds = (nw - ow) / _zoomspeedalfa; // ����������С
		double pres = ow;
		double nexs = ow + ds;

		// MapResult mr = _mapcontainer.Result;
		int size = 0;

		for (int i = 0; i < _zoomspeedalfa; i++) {
			// StopRefresh();
			for (MapImage mi : _mapResult.MapImages.values()) {
				if (size == 0)
					size = mi.size;
				mi.left = ((short) (Math.round((mi.left - px) * pres / nexs) + px));
				mi.top = ((short) (Math.round((mi.top - py) * pres / nexs) + py));
				mi.size = ((short) (Math.round(mi.size * pres / nexs) + 1));
			}

			invalidate();

			//
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.Threading.Thread.Sleep(50);
			pres += ds;
			nexs += ds;
		}
		// ��ԭ��ʼ��С
		for (MapImage mi : _mapResult.MapImages.values()) {
			mi.size = size;
		}
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * �¼�����
	 */

	// �������
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// ���������¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	// �켣��
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTrackballEvent(event);
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	// �û��ᴥ����������1��MotionEvent ACTION_DOWN����
	public boolean onDown(MotionEvent event) {
		// TODO Auto-generated method stub

		// Toast.makeText(fcontext, "Toast����", Toast.LENGTH_SHORT).show();

		// AlertDialog dialog = new AlertDialog.Builder(fcontext).create();
		// dialog.setTitle("DialogTest!");
		// dialog.show();

		if (mapParameter != null) {
			if (_startPointerOnce) {
				_startPointerOnce = false;

				double[] p = mapParameter.ClientToMap((int) event.getX(),
						(int) event.getY());
				_pPointerPos = new Point(p[0], p[1]);

			} else if (_action == ActionType.Pan
					|| _action == ActionType.ZoomInByClick
					|| _action == ActionType.ZoomOutByClick) {
				pre_mouse_x = (int) event.getX();
				pre_mouse_y = (int) event.getY();
				ismousedown = true;
				ispaned = false;
			}
		}
		return true;
	}

	private void setContentView(TextView tv) {
		// TODO Auto-generated method stub

	}

	// ����ƽ��
	// �û����´������������ƶ����ɿ�����1��MotionEvent ACTION_DOWN, ���ACTION_MOVE, 1��ACTION_UP����
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// �ƶ�����10�����أ���X����ÿ����ƶ��ٶȴ���20����ʱ�Ž��д���
		// if (Math.abs(e1.getX() - e2.getX()) > FLING_MIN_DISTANCE
		// && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
		// if (ismousedown) {
		// int dx = (int) (e2.getX() - e1.getX());
		// int dy = (int) (e2.getY() - e1.getY());
		//
		// if (!(dx == 0 && dy == 0)) {
		// Pan(dx, dy);
		//
		// ispaned = true;
		//
		// pre_mouse_x = (int) e2.getX();
		// pre_mouse_y = (int) e2.getY();
		// }
		// }
		// }

		return true;
	}

	// �û��������������ɶ��MotionEvent ACTION_DOWN����
	public void onLongPress(MotionEvent event) {
		// TODO Auto-generated method stub
		 _action = ActionType.ZoomInByClick;
		 if (!ispaned && mapService != null) {
		 Zoom(0.5, (int) event.getX(), (int) event.getY());
		 }

		// ��ʾͼ��
	}

	// �û����´����������϶�����1��MotionEvent ACTION_DOWN, ���ACTION_MOVE����
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
			float distanceX, float distanceY) {
		// TODO Auto-generated method stub

		// distanceX > 0 ��ʾ����
		// distanceY > 0 ��ʾ����
		int x = (int) distanceX;
		int y = (int) distanceY;
		Pan(-x, -y);
		return true;
	}

	// �û��ᴥ����������δ�ɿ����϶�����һ��1��MotionEvent ACTION_DOWN����
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	// �û����ᴥ���������ɿ�����һ��1��MotionEvent ACTION_UP����
	public boolean onSingleTapUp(MotionEvent event) {
		// TODO Auto-generated method stub

		if (_action == ActionType.Pan) {
			ismousedown = false;
		} else if (_action == ActionType.ZoomInByClick) {
			if (!ispaned && mapService != null) {
				Zoom(2.0, (int) event.getX(), (int) event.getY());
			}
			ismousedown = false;
		} else if (_action == ActionType.ZoomOutByClick) {
			if (!ispaned && mapService != null) {
				Zoom(0.5, (int) event.getX(), (int) event.getY());
			}
			ismousedown = false;
		}
		return true;
	}

}
