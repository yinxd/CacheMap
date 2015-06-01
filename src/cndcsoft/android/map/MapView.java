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

	public Bitmap _backgroundimage = null;// 背景图片
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
	private boolean _ishowpointer = false;// 信息点
	private Point _pPointerPos;
	// 双缓冲变量
	private Bitmap bitImage = null;
	private Bitmap bitBuffer = null;
	private Paint mPaint = null;
	// 线程管理
	public static final int REFRESH = 0x000001;
	private Handler mHandler = null;
	private Handler messageHandler = null;
	// 手势
	private GestureDetector mGestureDetector;
	private Scroller scroller;

	// 滑动距离和速度，较小距离不响应
	private static final int FLING_MIN_DISTANCE = 2;
	private static final int FLING_MIN_VELOCITY = 2;

	private Context fcontext;

	// 构造函数
	public MapView(Context context) {
		super(context);

	}

	// 构造函数，
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		fcontext = context;

		// 屏幕大小
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int Width = dm.widthPixels;
		int Height = dm.heightPixels;
		controlwidth = Width;
		controlheight = Height;

		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);// 去锯齿

		// 如果不打开，无法收到长时间点击，那么手势判断就无从进行
		mGestureDetector = new GestureDetector(this);// 手势
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
		// 背景
		Bitmap _backgroundimage = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg);
		if (_backgroundimage != null) {

			// 绘制背景图切片
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

	// 获取SD卡路径
	public static String getExternalStoragePath() {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();
		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return android.os.Environment.getExternalStorageDirectory()
						.getPath();
			}
		}
		return null;
	}

	// 获取存储卡的剩余容量，单位为字节
	public static long getAvailableStore(String filePath) {
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		// long totalBlocks = statFs.getBlockCount();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}

	// 负责绘制
	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (this) {
			canvas.drawColor(Color.WHITE);// 清屏
			bitImage = createBuffer();
			Canvas mCanvas = new Canvas(bitImage);
			if (_mapResult == null)
				return;
			// 开始绘制背景地图切片
			for (MapImage mi : _mapResult.MapImages.values()) {
				if (mi.Image == null)
					continue;
				// Rect destRect = new Rect(mi.left, mi.top,
				// mi.Image.getWidth(),
				// mi.Image.getHeight());
				// Rect srcRect = new Rect(0, 0, mi.Image.getWidth(), mi.Image
				// .getHeight());
				if (mi.Image.isRecycled()) {
					// <--报错的地方：
					Log.v("001", "this image object is aleadly recycled!!!"
							+ "----" + mi.id);
					continue;
				}
				// mCanvas.drawBitmap(mi.Image, destRect,srcRect ,
				// mPaint);//这个方法只绘制了左上角一小块区域。。
				mCanvas.drawBitmap(mi.Image, mi.left, mi.top, mPaint);
			}
			canvas.drawBitmap(bitImage, canvas.getClipBounds(), canvas
					.getClipBounds(), mPaint);
			// canvas.drawBitmap(bitImage, 0, 0, mPaint);
			// 开始绘制控制图层切片(类似googlemap定位效果)
			if (_ishowpointer) {
				if (_pPointerPos != null) {
					Paint mmPaint = new Paint();
					mmPaint.setStyle(Style.FILL);
					mmPaint.setColor(Color.RED);
					mmPaint.setAntiAlias(true);// 去锯齿
					Paint mmmPaint = new Paint();
					mmmPaint.setColor(Color.BLUE);
					mmmPaint.setAntiAlias(true);
					mmmPaint.setStyle(Style.STROKE);// 空心
					mmmPaint.setStrokeWidth(3);// 外框宽度
					
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

	// 加载地图数据
	public void LoadMap(MapServieType serviceType, Object[] paramlist) {
		/*
		 * 参数数组 (远程模式) " http://192.168.1.156:8090/MobileGISServer", //远程切片服务地址
		 * 
		 * @"\存储卡\MAP-DATA2", //本地保存路径 "yz0802_m" //地图名称
		 */
		invalidate();
		// 初始化地图服务(本地or远程)
		mapService = MapServiceFactory.getMapService(serviceType);
		if (paramlist.length == 2) {
			// 本地初始化
			// 参数：切片目录/地图名称/控件宽/控件高
			Object[] params = new Object[] { paramlist[0], paramlist[1],
					controlwidth, controlheight };
			mapService.Initialize(params);

		} else if (paramlist.length == 3) {
			// 远程初始化
			// 参数：切片服务地址/切片本地缓存路径/地图名称/控件宽/控件高
			Object[] params = new Object[] { paramlist[0], paramlist[1],
					paramlist[2], controlwidth, controlheight };
			mapService.Initialize(params);
		} else
			return;

		// 获取默认地图参数
		mapParameter = mapService.getDefaultMapParam();

		if (mapParameter == null) {
			return;
		}

		// 绘制地图
		refrashMap();
		invalidate();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 在收到消息时，对界面进行更新
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

	// 刷新地图
	private void setDety(MapImage mi) {
		if (mi == null)
			return;
		// postInvalidate(mi.left, mi.top, mi.size, mi.size);
		Rect rect = new Rect(mi.left, mi.top, mi.size, mi.size);
		Invalidate(rect);
	}

	private void Invalidate(Rect clipBounds) {
		clipBounds.offset(0, 0);// mapview为0
		// postInvalidate(clipBounds.left,clipBounds.top,clipBounds.right,clipBounds.right);
		postInvalidate();
	}

	public void startDrawLayers(boolean started) {
		if (started) {

		}

	}

	// 显示当前位置
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

	// 定位
	public void animateTo(Point mGeoPoint) {
		double[] ls = getLevels();
		Zoom(ls[ls.length - 1], mGeoPoint.X, mGeoPoint.Y);
	}

	// / <summary>
	// / 平移地图
	// / </summary>
	// / <param name="dx">x轴平移量</param>
	// / <param name="dy">y轴平移量</param>
	public void Pan(int dx, int dy) {
		if (mapParameter != null) {
			mapParameter.Pan(dx, dy);
			refrashMap();
		}
	}

	// / <summary>
	// / 缩放
	// / </summary>
	// / <param name="scaleFilter">缩放比例</param>
	// / <param name="px">屏幕缩放中心点</param>
	// / <param name="py">屏幕缩放中心点</param>
	public void Zoom(double scaleFilter, int px, int py) {
		if (mapParameter != null) {
			double ow = mapParameter.ViewBound.getWidth();
			mapParameter.Zoom(scaleFilter, px, py);
			double nw = mapParameter.ViewBound.getWidth();
			// 缩放效果
			zoomflash(ow, nw, px, py);
			Log.v("_____________", String.valueOf(ow)+":"+String.valueOf(nw));
			refrashMap();
		}
	}

	// / <summary>
	// / 缩放
	// / </summary>
	// / <param name="scale">比例尺</param>
	// / <param name="x">地图坐标中心点</param>
	// / <param name="y">地图坐标中心点</param>
	public void Zoom(double scale, double x, double y) {
		if (mapParameter != null) {
			mapParameter.ZoomByScaleCenter(scale, new Point(x, y));
			refrashMap();
		}
	}

	// / <summary>
	// / 执行扩展命令
	// / </summary>
	// / <param name="commandName">扩展命令名称</param>
	// / <param name="paramlist">命令参数</param>
	// / <returns>命令执行结果</returns>
	public String ExeExpandCommand(String commandName, Object[] paramlist) {
		return mapService.expandCommand(commandName, paramlist);
	}

	// / <summary>
	// / 获取或者设置地图当前操作类别
	// / </summary>
	public ActionType getMapAction() {
		return _action;
		// get { return _action; }
		// set { _action = value; }
	}

	// / <summary>
	// / 获取或者设置地图缩放滑动的阻力值
	// / </summary>
	public int getZoomSpeedAlfa() {
		return _zoomspeedalfa;
	}

	// / <summary>
	// / 获取当前地图名称
	// / </summary>
	public String getMapName() {

		if (mapParameter != null) {
			return mapParameter.MapName;
		}
		return "";
	}

	// / <summary>
	// / 获取缓存地图的比例尺层级，如果是非缓存地图则返回NULL
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
	// / 信息点
	// / </summary>
	public Point getPointerPosition() {
		return _pPointerPos;
	}

	// / <summary>
	// / 是否设置信息点
	// / </summary>
	public boolean getStartPointerOnce() {
		return _startPointerOnce;
	}

	// / <summary>
	// / 是否显示信息点
	// / </summary>
	public boolean IsShowPointer() {
		return _ishowpointer;
	}

	public Bitmap getbackgroundimage() {
		return _backgroundimage;

	}

	// 设置地图支持缩放，设置放大缩小图标按钮
	public boolean setBuiltInZoomControls(boolean isBuilt) {
		if (isBuilt) {
			return true;
		}
		return false;
	}

	// 交通模式
	public boolean setTraffic(boolean isshow) {
		if (isshow) {
			return true;
			// code
		}
		return false;
	}

	// 卫星
	public boolean setSatellite(boolean isshow) {
		if (isshow) {
			return true;
			// code
		}
		return false;
	}

	// 街景
	public boolean setStreetView(boolean isshow) {
		if (isshow) {
			return true;
			// code
		}
		return false;
	}

	/**
	 * 私有函数
	 */

	// / <summary>
	// / 缩小按钮事件
	// / </summary>
	void _abZoomOut_Event_MouseUp() {
		Zoom(0.5, clientcx, clientcy);
	}

	// / <summary>
	// / 放大按钮事件
	// / </summary>
	void _abZoomIn_Event_MouseUp() {
		Zoom(2.0, clientcx, clientcy);
	}

	/**
	 * 刷新地图
	 */
	private void refrashMap() {

		MapResult mapResult = mapService.getMapImage(mapParameter);
		_mapResult = mapResult;
		for (String newid : mapResult.newIDs) {
			final MapImage mi = mapResult.MapImages.get(newid);
			if (mi.Image == null) {
				// 把新的图片的获取任务加到线程中
				new Thread() {
					public void run() {
						Message msg = new Message();
						msg.what = 1;
						mi.getData();
						msg.obj = mi;
						//
						handler.sendMessage(msg);

						// 暂定50ms等待UI刷新？
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

		// 信息点
		// if (_ishowpointer)
		// {
		// int[] p = mapParameter.MapToClient(_pPointerPos.X,_pPointerPos.Y);
		// _abPointer.Left = p[0] - 5;
		// _abPointer.Top = p[1] - _abPointer.Height;
		// }

		invalidate();
	}

	// / <summary>
	// / 缩放效果
	// / </summary>
	// / <param name="ow">旧地图比例宽度</param>
	// / <param name="nw">新地图比例宽度</param>
	// / <param name="px">屏幕中心点</param>
	// / <param name="py">屏幕中心点</param>
	private void zoomflash(double ow, double nw, int px, int py) {
		if (_zoomspeedalfa < 1 || ow == nw)
			return;

		double ds = (nw - ow) / _zoomspeedalfa; // 步进比例大小
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
		// 还原初始大小
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
	 * 事件管理
	 */

	// 负责控制
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// 拦截手势事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	// 轨迹球
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTrackballEvent(event);
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	// 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
	public boolean onDown(MotionEvent event) {
		// TODO Auto-generated method stub

		// Toast.makeText(fcontext, "Toast测试", Toast.LENGTH_SHORT).show();

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

	// 滑动平移
	// 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// 移动超过10个像素，且X轴上每秒的移动速度大于20像素时才进行处理
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

	// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
	public void onLongPress(MotionEvent event) {
		// TODO Auto-generated method stub
		 _action = ActionType.ZoomInByClick;
		 if (!ispaned && mapService != null) {
		 Zoom(0.5, (int) event.getX(), (int) event.getY());
		 }

		// 显示图标
	}

	// 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
			float distanceX, float distanceY) {
		// TODO Auto-generated method stub

		// distanceX > 0 表示向左
		// distanceY > 0 表示向上
		int x = (int) distanceX;
		int y = (int) distanceY;
		Pan(-x, -y);
		return true;
	}

	// 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
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
