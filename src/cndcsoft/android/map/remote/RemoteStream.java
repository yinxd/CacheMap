package cndcsoft.android.map.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemoteStream {

	public String RemoteServerUrl;

	public RemoteStream(String url) {
		this.RemoteServerUrl = url;
	}

	// 拓展命令
	public String ExpandCommand(String SID, String commName, Object[] paramlist) {
		String c_url = RemoteServerUrl + "/EC?SID=" + SID + "&CN=" + commName
				+ "&CP=";

		if (paramlist.length > 0) {
			c_url += paramlist[0].toString();
			for (int i = 1; i < paramlist.length; i++) {
				c_url += "," + paramlist[i].toString();
			}
		}

		String strRes = "null";

		URL url = null;
		try {
			url = new URL(c_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 连接成功
				InputStreamReader isr = new InputStreamReader(con
						.getInputStream(), "utf-8");
				int i = 0;
				while ((i = isr.read()) != -1) {
					strRes = strRes + (char) i;
				}
				isr.close();
			}
			con.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strRes;
	}

	public void getSpaceImage(String SID, String mapName, String dstFilename) {
		String c_url = RemoteServerUrl + "/GMI?SID=" + SID + "&MN=" + mapName
				+ "&MI=s";

		URL url = null;
		try {
			File dir = new File(dstFilename.substring(0,dstFilename.lastIndexOf("/")+1));
			
			if(!dir.exists()){
				dir.mkdirs();
			}

			File file = new File(dstFilename);
			if (!file.exists()) {
				file.createNewFile();
			}
			

			url = new URL(c_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = con.getInputStream();

				byte[] bytes = new byte[1024];
		
				int len;

				OutputStream os = new FileOutputStream(dstFilename);

				while ((len = is.read(bytes)) != -1) {
					os.write(bytes, 0, len);
				}
				os.close();
				is.close();
			}
			con.disconnect();

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private Lock lock = new ReentrantLock();


	public boolean getImage(String SID, String mapName, String MID,
			String dstFilename) {
		boolean retrunme = true;
		String c_url = RemoteServerUrl + "/GMI?SID=" + SID + "&MN=" + mapName
				+ "&MI=" + MID;
		try {

			
			File dir = new File(dstFilename.substring(0,dstFilename.lastIndexOf("/")+1));
			
			if(!dir.exists()){
				dir.mkdirs();
			}

			File file = new File(dstFilename);
			if (!file.exists()) {

				file.createNewFile();
			}
			
			URL url = new URL(c_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = con.getInputStream();

				byte[] bytes = new byte[2048];

				int len;

				OutputStream os = new FileOutputStream(dstFilename);

				while ((len = is.read(bytes)) != -1) {
					os.write(bytes, 0, len);
				}
				os.close();
				is.close();
			}
			con.disconnect();

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			retrunme = false;
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			retrunme = false;
		}finally{
			
		}

		return retrunme;
	}

	public String getMapParameter(String SID, String mapName) {
		String c_url = RemoteServerUrl + "/GMP?SID=" + SID + "&MN=" + mapName;
		String strRes = "";
		
		URL url = null;
		HttpURLConnection con=null;
		
		try {
			url = new URL(c_url);
		    con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000); 
			con.setDoOutput(true);
			con.setDoInput(true); 
			con.setRequestMethod("POST");
			
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStreamReader isr = new InputStreamReader(con
						.getInputStream(), "utf-8");
				int i = 0;
				while ((i = isr.read()) != -1) {
					strRes = strRes + (char)i;
				}
				isr.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			con.disconnect();
		}
		return strRes;

	}
}
