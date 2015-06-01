package cndcsoft.android.map.remote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cndcsoft.android.map.lib.Envelope;
import cndcsoft.android.map.lib.IMapService;
import cndcsoft.android.map.lib.Level;
import cndcsoft.android.map.lib.MapImage;
import cndcsoft.android.map.lib.MapParam;
import cndcsoft.android.map.lib.MapResult;
import cndcsoft.android.map.local.LocalMapImage;
import cndcsoft.android.map.local.LocalMapParam;

public class RemoteMapService implements IMapService {

	public void Initialize(Object[] paramlist) {
		// TODO Auto-generated method stub

		Dispose();

		String server_url = paramlist[0].toString();
		filepath = paramlist[1].toString();
		mapname = paramlist[2].toString();
		int width = Integer.parseInt(paramlist[3].toString());
		int height = Integer.parseInt(paramlist[4].toString());

		remoteServ = new RemoteStream(server_url);

		String filename = filepath + "/" + mapname + ".xml";
		File file = new File(filename);
		if (file.exists()) {
			Document document = null;
			DocumentBuilder builder;
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			try {
				builder = builderFactory.newDocumentBuilder();
				document = builder.parse(file);
				_defaultMapParam = new RemoteMapParam(document);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				_defaultMapParam = new RemoteMapParam(remoteServ
						.getMapParameter(deviceID, mapname));
				_defaultMapParam.toFile(filename); // 写成XML
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

		_defaultMapParam.ClientWidth = width;
		_defaultMapParam.ClientHeight = height;

		_defaultMapParam.Entire();

		_defaultMapResult = new MapResult();

		String spaceImagePath = filepath + "/" + "spacer."
				+ _defaultMapParam.PicExt;
		File spacefile = new File(spaceImagePath);
		if (!spacefile.exists()) {
			remoteServ.getSpaceImage(deviceID, mapname, spaceImagePath);
		}

		spaceImage = BitmapFactory.decodeFile(spaceImagePath);
		_mapcache = new RemoteMapCache(
				(int) ((Math.round(width / (_defaultMapParam.PicSize * 2.0)) * 2 + 2) * ((Math
						.round(height / (_defaultMapParam.PicSize * 2.0)) * 2 + 2))) * 2);

		_oldids = new ArrayList<String>(64);
		_newids = new ArrayList<String>(64);
	}

	public void Dispose() {
		// TODO Auto-generated method stub

		if (_mapcache != null)
			_mapcache.Dispose();
		if (_defaultMapParam != null)
			_defaultMapParam = null;
		if (_defaultMapResult != null) {
			_defaultMapResult.Dispose();
			_defaultMapResult = null;
		}
		if (_newids != null)
			_newids.clear();
		;
		if (_oldids != null)
			_oldids.clear();
		if (spaceImage != null && !spaceImage.isRecycled())
			spaceImage.recycle();
	}

	public String expandCommand(String commName, Object[] paramlist) {
		// TODO Auto-generated method stub
		return remoteServ.ExpandCommand(deviceID, commName, paramlist);
	}

	public MapParam getDefaultMapParam() {
		// TODO Auto-generated method stub
		return _defaultMapParam;
	}

	public Level[] getLevel() {
		// TODO Auto-generated method stub
		return null;
	}
	// / <summary>
	// / 获取地图图片
	// / </summary>
	// / <param name="lmi">切片</param>
	void getimage(RemoteMapImage lmi) {
		if (lmi.isSpacer) {
			lmi.Image = this.spaceImage;
		} else {
			String filename = filepath + "/" + mapname + "/" + lmi.id + "."
					+ _defaultMapParam.PicExt;
			File file = new File(filename);
			if (file.exists()) {
				lmi.Image = BitmapFactory.decodeFile(filename);
			} else {
				if (remoteServ.getImage(deviceID, _defaultMapParam.MapName,
						lmi.id, filename)) {
					lmi.Image = BitmapFactory.decodeFile(filename);
				} else {
					lmi.Image = this.spaceImage;
					lmi.isSpacer = true;
				}
			}
		}
	}

	public MapResult getMapImage(MapParam mapParameter) {
		// TODO Auto-generated method stub
		RemoteMapParam lmp = (RemoteMapParam) mapParameter;

		double sw = lmp.levels[lmp.levelIndex].SWidth;
		int XMaxSize = lmp.levels[lmp.levelIndex].XMaxSize;
		int YMaxSize = lmp.levels[lmp.levelIndex].YMaxSize;
		short psize = lmp.PicSize;
		String PicExt = lmp.PicExt;
		String sscale = String.valueOf(lmp.Scale);
		Envelope mb = lmp.MapBound;
		Envelope vb = lmp.ViewBound;

		double dcountx_left = (vb.XMin - mb.XMin) / sw;
		double dcountx_right = (vb.XMax - mb.XMin) / sw;

		double dcounty_top = (mb.YMax - vb.YMax) / sw;
		double dcounty_bottom = (mb.YMax - vb.YMin) / sw;

		short xs = (short) Math.floor(dcountx_left);
		short xe = (short) Math.ceil(dcountx_right);

		short ys = (short) Math.floor(dcounty_top);
		short ye = (short) Math.ceil(dcounty_bottom);

		short xc = (short) (xe - xs + 1);
		short yc = (short) (ye - ys + 1);

		short lefts = (short) (-(int) ((dcountx_left - xs) * psize));
		short tops = (short) (-(int) ((dcounty_top - ys) * psize));

		HashMap<String, MapImage> mis = _defaultMapResult.MapImages;
		Set<String> keys = mis.keySet();
		if (keys != null) {
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				_oldids.add(key.toString());
			}
		}
		_newids.clear();

		for (short i = xs; i <= xe; i++) {
			for (short j = ys; j <= ye; j++) {

				short xi = (short) (i - xs);
				short yi = (short) (j - ys);

				String id = sscale.substring(0,sscale.lastIndexOf('.')) + "/" + i + "/" + j;

				if (mis.containsKey(id)) {
					MapImage mi = mis.get(id);
					mi.left = (short) (lefts + xi * psize);
					mi.top = (short) (tops + yi * psize);
					_oldids.remove(id);
				} else {
					RemoteMapImage mi = _mapcache.get(id);
					if (mi == null) {
						mi = _mapcache.getNewMapImage();
						mi._service_ref = this;

						mi.size = psize;
						mi.id = id;
						mi.left = (short) (lefts + xi * psize);
						mi.top = (short) (tops + yi * psize);

						mi.isSpacer = i < 0 || j < 0 || i > XMaxSize
								|| j > YMaxSize;

						mis.put(id, mi);
						_newids.add(id);


						_mapcache.add(id, mi);

					} else {
						mi.left = (short) (lefts + xi * psize);
						mi.top = (short) (tops + yi * psize);
						mis.put(id, mi);

						_newids.add(id);
					}

				}
			}
		}

		Object[] strs = _oldids.toArray();
		for (int i = 0; i < strs.length; i++) {
			mis.remove(strs[i].toString());
		}
		_oldids.clear();
		_defaultMapResult.newIDs = _newids;
		return _defaultMapResult;
	}

	private RemoteMapParam _defaultMapParam;
	private MapResult _defaultMapResult;
	Bitmap spaceImage;

	private RemoteStream remoteServ; 
	private String filepath;
	private String mapname;
	private RemoteMapCache _mapcache;
	private List<String> _oldids;
	private List<String> _newids;
	private String deviceID = "mobile-gis";


}
