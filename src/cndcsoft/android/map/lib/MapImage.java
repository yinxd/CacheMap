package cndcsoft.android.map.lib;

import android.graphics.Bitmap;

public abstract class MapImage {
	  /// <summary>
    /// 屏幕坐标X值
    /// </summary>
    public int left;
    /// <summary>
    /// 屏幕坐标Y值
    /// </summary>
    public int top;
    /// <summary>
    /// 图片大小（等宽高）
    /// </summary>
    public int size; 
    /// <summary>
    /// 切片ID
    /// </summary>
    public String id;
    /// <summary>
    /// 切片是否为空
    /// </summary>
    public boolean isSpacer;
    /// <summary>
    /// 切片图片
    /// </summary>
    //public Image Image = null;
    public Bitmap Image=null;

    /// <summary>
    /// 获取地图切片、必须要被继承
    /// </summary>
    public abstract void getData();

    public void dispose()
    {
        if (Image != null)
        {
            //Image.Dispose();
            Image = null;
        }
    }
}
