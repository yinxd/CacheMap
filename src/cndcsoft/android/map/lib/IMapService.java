package cndcsoft.android.map.lib;

public interface IMapService {

	/// <summary>
    /// 地图服务初始化函数
    /// </summary>
    /// <param name="paramlist">初始化参数</param>
    void Initialize(Object[] paramlist);
    
    /// <summary>
    /// 获取地图默认参数
    /// </summary>
    /// <returns>返回地图默认参数</returns>
    MapParam getDefaultMapParam();
    /// <summary>
    /// 获取地图
    /// </summary>
    /// <param name="mapParameter">地图参数</param>
    /// <returns>返回地图对象</returns>
    MapResult getMapImage(MapParam mapParameter);
    /// <summary>
    /// 扩展命令调用，只在远程服务中实现了
    /// </summary>
    /// <param name="commName">扩展命令名称</param>
    /// <param name="paramlist">命令参数</param>
    /// <returns>命令返回结果</returns>
    String expandCommand(String commName, Object[] paramlist);
    /// <summary>
    /// 释放资源
    /// </summary>
    
    Level[] getLevel();

    void Dispose();
}
