package cndcsoft.android.map.local;

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
import android.util.Log;

public class LocalMapCache {

	  public LocalMapCache(int initSize){
		  
		  cacheSize = initSize;//48
          newImageSize = initSize;
          images= new HashMap<String,LocalMapImage>(cacheSize);
          newimages = new LinkedList<LocalMapImage>();
          ids = new ArrayList<String>(cacheSize);

          delTimer = new Timer(true);
          delTimer.schedule(tcb, 100, 3000);
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

              LocalMapImage lmi = images.get(oid);
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
              LocalMapImage mi = new LocalMapImage();
              newimages.add(mi);
          }

      }
     
      public LocalMapImage get(String id)
      {
          if (images.containsKey(id))
          {
              ids.remove(id);
              ids.add(id);

              return images.get(id);
          }
          return null;
      }
	 
      public void add(String id, LocalMapImage mi)
      {
          if (images.containsKey(id)) return;

          ids.add(id);
          images.put(id, mi);
          Log.v("cache_add", id);
      }
      
      public void Dispose()
      {
    	  delTimer.cancel();//ÍË³ö¼ÆÊ±Æ÷
    	  Set<String> keys = images.keySet();
    	  if(keys!=null){
    		  Iterator<String> iterator = keys.iterator();
    		  while(iterator.hasNext()){
    			  Object key = iterator.next();
    			  LocalMapImage lmi = images.get(key);
    			  if(lmi.Image!=null&& !lmi.Image.isRecycled()){
    				  lmi.Image.recycle();
    			  }
    		  }
    	  }
          images.clear();
          ids.clear();
          newimages.clear();
      }
   
      public LocalMapImage getNewMapImage()
      {
          if (newimages.size() > 0)
          {
              return newimages.remove();
          }
          else
          {
              return new LocalMapImage();
          }
      }
      
	  private Timer delTimer;

      private int cacheSize = 10;
      private int newImageSize = 5;
      private HashMap<String, LocalMapImage> images;
      private List<String> ids;
      private Queue<LocalMapImage> newimages;
}
