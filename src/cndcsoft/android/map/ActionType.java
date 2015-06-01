package cndcsoft.android.map;

public enum ActionType {
	  /// <summary>
    /// 平移
    /// </summary>
    Pan, 
    /// <summary>
    /// 点击放大
    /// </summary>
    ZoomInByClick,  
    /// <summary>
    /// 点击缩小
    /// </summary>
    ZoomOutByClick,
 
    /// <summary>
    /// 拉框放大（待拓展）
    /// </summary>
    ZoomInByRect,

    /// <summary>
    /// 拉框缩小（待拓展）
    /// </summary>
    ZoomOutByRect,

    /// <summary>
    /// 我的位置(GPS/LBS-待拓展)
    /// </summary>
    LocationMe

}
