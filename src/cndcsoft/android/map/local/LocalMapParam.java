package cndcsoft.android.map.local;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cndcsoft.android.map.lib.Envelope;
import cndcsoft.android.map.lib.Level;
import cndcsoft.android.map.lib.MapParam;
import cndcsoft.android.map.lib.Point;

public class LocalMapParam extends MapParam {
	// / <summary>
	// / ��ͼ�����ߵȼ�����
	// / </summary>
	public Level[] levels;
	// / <summary>
	// / ��ͼ��Ƭ��С
	// / </summary>
	public short PicSize;
	// / <summary>
	// / ��ͼ��ǰ�����ߵȼ�����
	// / </summary>
	public short levelIndex;
	// / <summary>
	// / ��ͼ��ƬͼƬ��ʽ
	// / </summary>
	public String PicExt;

	// / <summary>
	// / ȫͼ
	// / </summary>
	@Override
	public void Entire() {
		levelIndex = 0;
		Scale = levels[levelIndex].Scale;
		double sw = levels[levelIndex].SWidth;
		
		Point pcenter = MapBound.getCenter();

		double xdis = sw * ClientWidth / (PicSize * 2.0);
		double ydis = sw * ClientHeight / (PicSize * 2.0);
		ViewBound = new Envelope(pcenter.X - xdis,pcenter.X + xdis,pcenter.Y - ydis,pcenter.Y + ydis);
	}

	// / <summary>
	// / ƽ��
	// / </summary>
	// / <param name="dx">x��ƽ����</param>
	// / <param name="dy">y��ƽ����</param>
	@Override
	public void Pan(int dx, int dy) {
		double sw = levels[levelIndex].SWidth;

		double xdis = dx * sw / (PicSize * 1.0);
		double ydis = dy * sw / (PicSize * 1.0);

		ViewBound.XMin -= xdis;
		ViewBound.XMax -= xdis;
		ViewBound.YMin += ydis;
		ViewBound.YMax += ydis;
	}

	// / <summary>
	// / �ı��ͼ��Ұ��С
	// / </summary>
	// / <param name="width">�µĿ��</param>
	// / <param name="height">�µĸ߶�</param>
	@Override
	public void Resize(int width, int height) {
		ClientWidth = width;
		ClientHeight = height;

		double sw = levels[levelIndex].SWidth;
		Point pcenter = ViewBound.getCenter();

		double xdis = sw * ClientWidth / (PicSize * 2.0);
		double ydis = sw * ClientHeight / (PicSize * 2.0);
		ViewBound.XMin = pcenter.X - xdis;
		ViewBound.XMax = pcenter.X + xdis;
		ViewBound.YMin = pcenter.Y - ydis;
		ViewBound.YMax = pcenter.Y + ydis;
	}

	// / <summary>
	// / ����
	// / </summary>
	// / <param name="scale">��������</param>
	// / <param name="x">��Ļ���ĵ�x</param>
	// / <param name="y">��Ļ���ĵ�y</param>
	@Override
	public void Zoom(double scale, int x, int y) {
		if (scale <= 0)
			return;

		double osw = levels[levelIndex].SWidth;

		int l = levelIndex;
		if (scale > 1) {
			l = l + (int) (Math.round(scale - 1));
		} else if (scale < 1) {
			l = l - (int) (1.0 / scale - 1);
		}
		if (l > 0) {
			levelIndex = ((short) (l < levels.length ? l : levels.length - 1));
		} else {
			levelIndex = 0;
		}

		Scale = levels[levelIndex].Scale;
		Point pcenter = ViewBound.getCenter();
		double nsw = levels[levelIndex].SWidth;
		//
		double ncx = pcenter.X + (osw - nsw) * (2.0 * x - ClientWidth)
				/ (PicSize * 2.0);
		double ncy = pcenter.Y + (nsw - osw) * (2.0 * y - ClientHeight)
				/ (PicSize * 2.0);

		double xdis = nsw * ClientWidth / (PicSize * 2.0);
		double ydis = nsw * ClientHeight / (PicSize * 2.0);
		ViewBound.XMin = ncx - xdis;
		ViewBound.XMax = ncx + xdis;
		ViewBound.YMin = ncy - ydis;
		ViewBound.YMax = ncy + ydis;
	}

	// / <summary>
	// / ���ݱ��������ĵ㶨λ
	// / </summary>
	// / <param name="scale"></param>
	// / <param name="center"></param>
	@Override
	public void ZoomByScaleCenter(double scale, Point center) {
		if (scale <= 0)
			return;

		int li = 0;
		double s = levels[0].Scale;
		double dtmp = Math.abs(scale - s);
		for (int i = li + 1; i < levels.length; i++) {
			Level l = levels[i];
			double ndtmp = Math.abs(l.Scale - s);
			if (ndtmp < dtmp) {
				li = i;
				s = l.Scale;
				dtmp = ndtmp;
			}
		}

		levelIndex = (short) li;
		Scale = s;

		double nsw = levels[levelIndex].SWidth;
		double xdis = nsw * ClientWidth / (PicSize * 2.0);
		double ydis = nsw * ClientHeight / (PicSize * 2.0);
		//
		ViewBound.XMin = center.X - xdis;
		ViewBound.XMax = center.X + xdis;
		ViewBound.YMin = center.Y - ydis;
		ViewBound.YMax = center.Y + ydis;
	}

	// / <summary>
	// / ��ǰ�������£�ÿ��Ƭ��С����ĵ�ͼ���
	// / </summary>
	// / <returns></returns>
	public double getSWidth() {
		return levels[levelIndex].SWidth;
	}

	public LocalMapParam() {
	}

	public LocalMapParam(Document xmlDoc) {
		NodeList mapSetting = xmlDoc.getElementsByTagName("mapSetting");
		Element element = (Element) mapSetting.item(0);
		MapName = element.getAttribute("AliasName");
		PicSize = Short.parseShort(element.getAttribute("PicSize"));
		PicExt = element.getAttribute("PicExt");
		
		NodeList MapEnvelop= xmlDoc.getElementsByTagName("MapEnvelope");
		double XMIN = Double.parseDouble(((Element)MapEnvelop.item(0)).getAttribute("XMIN"));
		double XMAX = Double.parseDouble(((Element)MapEnvelop.item(0)).getAttribute("XMAX"));
		double YMIN = Double.parseDouble(((Element)MapEnvelop.item(0)).getAttribute("YMIN"));
		double YMAX = Double.parseDouble(((Element)MapEnvelop.item(0)).getAttribute("YMAX"));
		
		NodeList Scales= xmlDoc.getElementsByTagName("Scale");
		Level[] Levels= new Level[Scales.getLength()];
		double XDIS = XMAX - XMIN;
        double YDIS = YMAX - YMIN;
        
        for(int i=0;i<Levels.length;i++){
        	
        	Element levelement = (Element)Scales.item(i);
        	Levels[i] = new Level();
        	Levels[i].Index= Integer.parseInt(levelement.getAttribute("Level"));
        	Levels[i].Scale=Double.parseDouble(levelement.getAttribute("Value"));
        	Levels[i].SWidth=Double.parseDouble(levelement.getAttribute("SWidth"));
        	Levels[i].XMaxSize=(short)(Math.round(XDIS / Levels[i].SWidth));
        	Levels[i].YMaxSize =(short)(Math.round(YDIS / Levels[i].SWidth));
        }
        
        // ����Ĭ�ϵ�ͼ
        IsCahced = true;
        levels = Levels;
        MapBound = new Envelope(XMIN, XMAX, YMIN, YMAX);
	}

}


