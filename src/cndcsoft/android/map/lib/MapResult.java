package cndcsoft.android.map.lib;

import java.util.HashMap;
import java.util.List;

public class MapResult {

	//public Dictionary<string,MapImage> MapImages = new Dictionary<string,MapImage>();
	  /// ��Ƭ����
	public HashMap<String,MapImage> MapImages=new HashMap<String,MapImage>();
	
	  /// <summary>
    /// �½�����Ƭ
    /// </summary>
    public List<String> newIDs;

    /// <summary>
    /// ����
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
