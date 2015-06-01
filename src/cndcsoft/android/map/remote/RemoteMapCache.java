package cndcsoft.android.map.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class RemoteMapCache {

	  public RemoteMapCache(int initSize){
		  cacheSize = initSize;
          newImageSize = initSize;

          images= new HashMap<String,RemoteMapImage>(cacheSize);   
          newimages = new LinkedList<RemoteMapImage>();
          
          ids = new ArrayList<String>(cacheSize);

          delTimer = new Timer(true);
          delTimer.schedule(tcb, 100, 3000);//ÿ3���ӣ����һ�λ���

	  }
	  
	  TimerTask tcb =new TimerTask(){
		  public void run(){
			  Message message = new Message();
			  message.what=1;
			  
			  handler.sendMessage(message);
		  }
	  };
	  
	  final Handler handler = new Handler(){
		  public void handleMessage(Message msg){
			  switch (msg.what){
			  case 1:
				  delTimer_Tick(null);
				  break;
			  }
			  super.handleMessage(msg);
		  }
	  };
	  
      void delTimer_Tick(Object obj)
      {
          for (int i = ids.size() - cacheSize; i > 0; i--)
          {
              String oid = ids.get(0);

              RemoteMapImage lmi = images.get(oid);
              if (lmi.Image != null && !lmi.isSpacer)
              {
            	  if(!lmi.Image.isRecycled()){
            		  lmi.Image.recycle();	  
            	  }
              }
              images.remove(oid);
              ids.remove(0);
          }
          for (int i = newImageSize - newimages.size(); i > 0; i--)
          {
              RemoteMapImage mi = new RemoteMapImage();
              newimages.add(mi);
          }

      }
      
      /// <summary>
      /// ��ȡ��Ƭ�������
      /// </summary>
      /// <param name="id">��ƬID</param>
      /// <returns></returns>
      public RemoteMapImage get(String id)
      {
          if (images.containsKey(id))
          {
              ids.remove(id);
              ids.add(id);

              return images.get(id);
          }
          return null;
      }
	  
      /// <summary>
      /// �����Ƭ�������
      /// </summary>
      /// <param name="id"></param>
      /// <param name="mi"></param>
      public void add(String id, RemoteMapImage mi)
      {
          if (images.containsKey(id)) return;

          ids.add(id);
          images.put(id, mi);
      }
      
      /// <summary>
      /// ����
      /// </summary>
      public void Dispose()
      {
    	  delTimer.cancel();//�˳���ʱ��
    	  Set<String> keys = images.keySet();
    	  if(keys!=null){
    		  Iterator<String> iterator = keys.iterator();
    		  while(iterator.hasNext()){
    			  Object key = iterator.next();
    			  RemoteMapImage lmi = images.get(key);
    			  if(lmi.Image!=null&& !lmi.Image.isRecycled()){
    				  lmi.Image.recycle();
    			  }
    		  }
    	  }
          images.clear();
          ids.clear();
          newimages.clear();
      }
      
      
      /// <summary>
      /// ��ȡ����Ƭ�������
      /// </summary>
      /// <returns></returns>
      public RemoteMapImage getNewMapImage()
      {
          if (newimages.size() > 0)
          {
        	  //ɾ��������Queue�е�ͷԪ��
              return newimages.remove();
          }
          else
          {
              return new RemoteMapImage();
          }
      }
      
	  private Timer delTimer;

      private int cacheSize = 20;
      private int newImageSize = 10;
      private HashMap<String, RemoteMapImage> images;
      private List<String> ids;
      private Queue<RemoteMapImage> newimages;
}
