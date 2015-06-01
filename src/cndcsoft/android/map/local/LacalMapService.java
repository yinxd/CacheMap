package cndcsoft.android.map.local;

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
import android.util.Log;
import cndcsoft.android.map.lib.Envelope;
import cndcsoft.android.map.lib.IMapService;
import cndcsoft.android.map.lib.Level;
import cndcsoft.android.map.lib.MapImage;
import cndcsoft.android.map.lib.MapParam;
import cndcsoft.android.map.lib.MapResult;

public class LacalMapService implements IMapService {

	public void Initialize(Object[] paramlist) {
		// TODO Auto-generated method stub

		Dispose();
		filePath = paramlist[0].toString();
		mapname = paramlist[1].toString();
		int width = Integer.parseInt(paramlist[2].toString());
		int height = Integer.parseInt(paramlist[3].toString());

		File file = new File(filePath + "/" + mapname + ".xml");

		Document document = null;
		DocumentBuilder builder;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(file);
			_defaultMapParam = new LocalMapParam(document);
			_defaultMapParam.ClientWidth = width;
			_defaultMapParam.ClientHeight = height;
			_defaultMapParam.Entire();
			_defaultMapResult = new MapResult();
			spaceImage = BitmapFactory.decodeFile(filePath + "/" + "spacer."
					+ _defaultMapParam.PicExt);
			_mapcache = new LocalMapCache(
					(int) ((Math
							.round(width / (_defaultMapParam.PicSize * 2.0)) * 2 + 2) * ((Math
							.round(height / (_defaultMapParam.PicSize * 2.0)) * 2 + 2))) * 2);
			_oldids = new ArrayList<String>(64);
			_newids = new ArrayList<String>(64);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String expandCommand(String commName, Object[] paramlist) {
		// TODO Auto-generated method stub
		return null;
	}

	public MapParam getDefaultMapParam() {
		// TODO Auto-generated method stub
		return _defaultMapParam;
	}

	public Level[] getLevel() {
		// TODO Auto-generated method stub
		return _defaultMapParam.levels;
	}
	
	public MapResult getMapImage(MapParam _mapParameter) {
		// TODO Auto-generated method stub
		LocalMapParam lmp = (LocalMapParam) _mapParameter;

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

				String id = sscale.substring(0, sscale.lastIndexOf('.')) + "/"
						+ i + "/" + j;
				
				if (mis.containsKey(id)) {
					MapImage mi = mis.get(id);
					mi.left = (short) (lefts + xi * psize);
					mi.top = (short) (tops + yi * psize);
					_oldids.remove(id);
					Log.v("oldhas", "old_has"+":"+id);
				} else {
					LocalMapImage mi = _mapcache.get(id);
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

	public void Dispose() {
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

	void getimage(LocalMapImage lmi) {
		if (lmi.isSpacer) {
			lmi.Image = this.spaceImage;
		} else {
			String filename = filePath + "/" + mapname + "/" + lmi.id + "."
					+ _defaultMapParam.PicExt;
			File file = new File(filename);
			if (file.exists()) {
				lmi.Image = BitmapFactory.decodeFile(filename);
			} else {
				lmi.Image = this.spaceImage;
				lmi.isSpacer = true;
			}
		}
	}

	private LocalMapParam _defaultMapParam;
	private MapResult _defaultMapResult;
	Bitmap spaceImage;

	private String filePath;
	private String mapname;
	private LocalMapCache _mapcache;
	private List<String> _oldids;
	private List<String> _newids;


}
