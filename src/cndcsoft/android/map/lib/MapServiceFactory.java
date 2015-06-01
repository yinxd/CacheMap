package cndcsoft.android.map.lib;

import cndcsoft.android.map.local.LacalMapService;
import cndcsoft.android.map.remote.RemoteMapService;

public class MapServiceFactory {
	 /// <summary>
    /// 获取地图服务
    /// </summary>
    /// <param name="type">地图类别</param>
    /// <returns>返回地图服务对象</returns>
    public static IMapService getMapService(MapServieType type){
        if (type == MapServieType.LocalMapService) return new LacalMapService();
        else if (type == MapServieType.RemoteMapService) 
        	return new RemoteMapService();
        return null;
    }
    
    /// <summary>
    /// 地图服务类别
    /// </summary>
    public enum MapServieType { 
        /// <summary>
        /// 本地缓存地图服务
        /// </summary>
        LocalMapService,
        /// <summary>
        /// 服务器端地图服务
        /// </summary>
        RemoteMapService
    }
}
