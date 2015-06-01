package cndcsoft.android.map.lib;

import cndcsoft.android.map.local.LacalMapService;
import cndcsoft.android.map.remote.RemoteMapService;

public class MapServiceFactory {
	 /// <summary>
    /// ��ȡ��ͼ����
    /// </summary>
    /// <param name="type">��ͼ���</param>
    /// <returns>���ص�ͼ�������</returns>
    public static IMapService getMapService(MapServieType type){
        if (type == MapServieType.LocalMapService) return new LacalMapService();
        else if (type == MapServieType.RemoteMapService) 
        	return new RemoteMapService();
        return null;
    }
    
    /// <summary>
    /// ��ͼ�������
    /// </summary>
    public enum MapServieType { 
        /// <summary>
        /// ���ػ����ͼ����
        /// </summary>
        LocalMapService,
        /// <summary>
        /// �������˵�ͼ����
        /// </summary>
        RemoteMapService
    }
}
