package cndcsoft.android.map.lib;

import java.util.HashMap;
import java.util.List;

public class MapResult {

	//public Dictionary<string,MapImage> MapImages = new Dictionary<string,MapImage>();
	  /// 切片集合
	public HashMap<String,MapImage> MapImages=new HashMap<String,MapImage>();
	
	  /// <summary>
    /// 新建的切片
    /// </summary>
    public List<String> newIDs;

    /// <summary>
    /// 析构
    /// </summary>
    public void Dispose()
    {
        newIDs.clear();
        for (int i = 0; i < MapImages.size(); i++)
        {
            //MapImages[
            //MapImages[i] = null;
        }
        MapImages.clear();
    }
}
