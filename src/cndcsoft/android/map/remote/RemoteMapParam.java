package cndcsoft.android.map.remote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import cndcsoft.android.map.lib.Envelope;
import cndcsoft.android.map.lib.Level;
import cndcsoft.android.map.lib.MapParam;
import cndcsoft.android.map.lib.Point;
import cndcsoft.android.map.local.LocalMapParam;

public class RemoteMapParam extends LocalMapParam {
	
	public int InDate = -1;
	
	 /// <summary>
    /// 服务端获取XML
    /// </summary>
    /// <param name="remoteString"></param>
    public RemoteMapParam(String remoteString) throws Exception{
    	 if (remoteString.equals("null")||remoteString=="")
         {
             throw new Exception("指定的地图名称错误!");
         }
    	  String[] strs = remoteString.split(",");
          MapName = strs[0];
          PicExt = strs[1];
          
          PicSize = Short.parseShort(strs[2]);
          InDate = Integer.parseInt(strs[3]);

          double XMax = Double.parseDouble(strs[4]);
          double XMin = Double.parseDouble(strs[5]);
          double YMax = Double.parseDouble(strs[6]);
          double YMin = Double.parseDouble(strs[7]);

          MapBound = new Envelope(XMin, XMax, YMin, YMax);

          double XDIS = XMax - XMin;
          double YDIS = YMax - YMin;

          levels = new Level[(strs.length - 8) / 2];//9
          for(int i=8,j=0;i<strs.length;j++,i+=2){
        	  levels[j] = new Level();
              levels[j].Index =j;
              levels[j].SWidth = Double.parseDouble(strs[i]);
              levels[j].Scale = Double.parseDouble(strs[i + 1]);
              levels[j].XMaxSize = (short)(Math.round(XDIS / levels[j].SWidth));
              levels[j].YMaxSize = (short)(Math.round(YDIS / levels[j].SWidth));
          }

          IsCahced = true;
    }
    
    public RemoteMapParam(Document xmlDoc){
    	
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
        
        // 建立默认地图
        IsCahced = true;
        levels = Levels;
        MapBound = new Envelope(XMIN, XMAX, YMIN, YMAX);
        

		if(element.hasAttribute("InDate")){
			InDate=Integer.parseInt(element.getAttribute("InDate"));
		}
    }

    public byte[] createXML() throws Exception{
    	XmlSerializer serializer = Xml.newSerializer();
    	ByteArrayOutputStream writer = new ByteArrayOutputStream();
    	try{
    		serializer.setOutput(writer,"UTF-8");
    		serializer.startDocument("UTF-8", true);
    		serializer.startTag("", "mapSetting");
    		serializer.attribute("", "AliasName", MapName);
    		serializer.attribute("", "PicExt", PicExt);
    		serializer.attribute("", "PicSize",String.valueOf(PicSize));
    		serializer.startTag("", "MapEnvelope");
    		serializer.attribute("", "XMAX",String.valueOf(MapBound.XMax));
    		serializer.attribute("", "XMIN", String.valueOf(MapBound.XMin));
    		serializer.attribute("", "YMAX",String.valueOf(MapBound.YMax));
    		serializer.attribute("", "YMIN", String.valueOf(MapBound.YMin));
    		serializer.endTag("", "MapEnvelope");
    		serializer.startTag("", "Scales");
    		for(int i=0;i<levels.length;i++){
    			serializer.startTag("", "Scale");
    			serializer.attribute("", "Level",String.valueOf(levels[i].Index));
    			serializer.attribute("", "SWidth", String.valueOf(levels[i].SWidth));
    			serializer.attribute("", "Value", String.valueOf(levels[i].Scale));
    			serializer.endTag("", "Scale");
    		}
    		serializer.endTag("", "Scales");
    		serializer.endTag("", "mapSetting");
    		serializer.endDocument();
    		
    		writer.flush();
    		return writer.toByteArray();

    	}catch(Exception e){
    		throw new Exception(e);
    	}
    }
    
    //远程读取信息写入XML文件
    public void toFile(String fileName) throws Exception{
    	
    	File xmlFile = new File(fileName);
    	if(xmlFile.exists()){
    		xmlFile.delete();
    		
    	}
    	try{   	
    		xmlFile.createNewFile();
    	}
    	catch(IOException e){
    		throw new Exception(e);
    	}
    	FileOutputStream fileos = null;
    	try{
    		byte[] bytes = createXML();
    		fileos = new FileOutputStream(xmlFile);
    		fileos.write(bytes);
    		fileos.flush();
    		fileos.close();	
    	}catch(Exception e){		
    	}
    }

    
}