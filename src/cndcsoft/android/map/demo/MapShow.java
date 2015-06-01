package cndcsoft.android.map.demo;

import cndcsoft.android.map.MapView;
import cndcsoft.android.map.OwnView;
import cndcsoft.android.map.lib.MapParam;
import cndcsoft.android.map.lib.MapServiceFactory.MapServieType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MapShow extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

//		OwnView myView = new OwnView(this);
//		setContentView(myView);
		
		mapview = new MapView(this, null);
		SDCardDir = getExternalStoragePath();
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.main_land);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.main);
		}
		setViews();
		loadData();
		
//		// 接收参数
//		Bundle bundle = this.getIntent().getExtras();
//		if (bundle != null) {
//			double x = bundle.getDouble("X");
//			double y = bundle.getDouble("Y");
//			if (x > 0 && y > 0) {
//				mapview.ShowLocation(x, y);
//			}
//		}

	}

	private ImageButton btnZoomin,btnZoomout,btnLocation;
	private TextView txtMapInfo;
	MapView mapview;
	String SDCardDir = "";
	Location loc;
	LocationManager locM;
	int chooseItem = 0;

	// 显示模式
	private final static int MAP_SHOW_MODE_OPTION = 1;
	// 雨量
	private final static int RAIN_SHOW_MODE_OPTION = 2;
	// 退出程序
	private final static int EXIT_MAP_OPTION = 3;

	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	private void setViews() {
		mapview = (MapView) findViewById(R.id.mapview01);
		btnZoomin = (ImageButton) findViewById(R.id.zoomin);
		btnZoomout = (ImageButton) findViewById(R.id.zoomout);
		btnLocation =(ImageButton)findViewById(R.id.location);
		txtMapInfo = (TextView) findViewById(R.id.mapinfo);
		

		btnZoomin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 放大
				int y = mapview.controlheight;
				int x = mapview.controlwidth;
				//mapview.Zoom(2.0, x / 2, y / 2);
			}

		});

		btnZoomout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 缩小
				int y = mapview.controlheight;
				int x = mapview.controlwidth;
				//mapview.Zoom(0.5, x / 2, y / 2);
			}
		});
		
		btnLocation.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//定位当前位置
				mapview.ShowLocation(121.67582, 29.762121);
			}
		});
	}

	private void loadData() {
		 Object[] params = new Object[] { SDCardDir + "/map-data2", "yz0802_m"
		 };
		 mapview.LoadMap(MapServieType.LocalMapService, params);
//		Object[] params = new Object[] {
//				"http://192.168.1.110:8080/MobileGISServer/",
//				SDCardDir + "/map-data-yz", "yz0802_m" };
//		mapview.LoadMap(MapServieType.RemoteMapService, params);
		txtMapInfo.setText("浙江.宁波.鄞州区交通图");
	}

	// 获取路径
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

	// 获取位置
	public void getLocaton() {

		locM = (LocationManager) getSystemService(LOCATION_SERVICE);
		// Criteria 标准
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String provider = locM.getBestProvider(criteria, true);
		loc = locM.getLastKnownLocation(provider);
	}

	public void selectMapShowMode() {
		Dialog d = new AlertDialog.Builder(this).setTitle("选择显示").setItems(
				R.array.show_mode, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						chooseItem = which;
						switch (chooseItem) {
						case 0:
							// 设置为街景模式
							DisplayToast("暂无数据");
							break;
						case 1:
							// 设置为卫星模式
							DisplayToast("暂无数据");
							break;
						case 2:
							// 交通模式
							break;
						default:
							break;
						}
					}
				}).setNeutralButton("返回",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();

		d.show();
	}

	public void selectRainShowMode() {
		Dialog d = new AlertDialog.Builder(this).setTitle("选择显示").setItems(
				new String[] { "1H雨量", "3H雨量", "12H雨量", "24H雨量" },
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						chooseItem = which;
						switch (chooseItem) {
						case 0:
							MapShow.CURRENT_SHOW_TYPE = MapShow.RAIN_LIMIT_SHOW_TYPE[1];
							// mMapController.setZoom(mMapView.getZoomLevel());
							mapview.Zoom(1, mapview.controlwidth / 2,
									mapview.controlheight / 2);
							break;
						case 1:
							MapShow.CURRENT_SHOW_TYPE = MapShow.RAIN_LIMIT_SHOW_TYPE[2];
							// mMapController.setZoom(mMapView.getZoomLevel());
							mapview.Zoom(1, mapview.controlwidth / 2,
									mapview.controlheight / 2);
							break;
						case 2:
							MapShow.CURRENT_SHOW_TYPE = MapShow.RAIN_LIMIT_SHOW_TYPE[3];
							// mMapController.setZoom(mMapView.getZoomLevel());
							mapview.Zoom(1, mapview.controlwidth / 2,
									mapview.controlheight / 2);
							break;
						case 3:
							MapShow.CURRENT_SHOW_TYPE = MapShow.RAIN_LIMIT_SHOW_TYPE[4];
							// mMapController.setZoom(mMapView.getZoomLevel());
							mapview.Zoom(1, mapview.controlwidth / 2,
									mapview.controlheight / 2);
							break;
						default:
							break;
						}
					}
				}).setNeutralButton("返回",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).create();

		d.show();

	}

	public static String[] RAIN_LIMIT_SHOW_TYPE = new String[] { "all", "1",
			"3", "12", "24" };
	public static String CURRENT_SHOW_TYPE = RAIN_LIMIT_SHOW_TYPE[0];
	public static String locationStationCode = "";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MAP_SHOW_MODE_OPTION, 0, "显示模式");
		menu.add(0, RAIN_SHOW_MODE_OPTION, 1, "雨量站点");
		menu.add(0, EXIT_MAP_OPTION, 2, "全部站点");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MAP_SHOW_MODE_OPTION:
			selectMapShowMode();
			break;
		case EXIT_MAP_OPTION:
			MapShow.CURRENT_SHOW_TYPE = MapShow.RAIN_LIMIT_SHOW_TYPE[0];
			// mMapController.setZoom(mMapView.getZoomLevel());
			mapview
					.Zoom(1, mapview.controlwidth / 2,
							mapview.controlheight / 2);
			break;
		case RAIN_SHOW_MODE_OPTION:
			selectRainShowMode();
			// showtest();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void showtest() {
		Intent intent = new Intent("nb.cndcsoft.cachemap.MapView01");
		final ComponentName cn = new ComponentName("nb.cndcsoft.cachemap",
				"nb.cndcsoft.cachemap.MainInfo");
		intent.setComponent(cn);

		// 定位到某一个水库的参数//该水库经纬度
		Bundle bundle = new Bundle();
		bundle.putString("OK", "hello");
		intent.putExtras(bundle);
		startActivity(intent);
	}

}