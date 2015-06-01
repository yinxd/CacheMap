package cndcsoft.android.map.lib;


/**
 * @author xiaox
 * 
 *
 */
public class Envelope {

	///最小X值
    public double XMin;

    /// 最小Y值
    public double YMin;

    /// 最大X值
    public double XMax;

    /// 最大Y值
    public double YMax;
    
    /// <summary>
    /// 构造函数
    /// </summary>
    /// <param name="xmin">最小X值</param>
    /// <param name="xmax">最大X值</param>
    /// <param name="ymin">最小Y值</param>
    /// <param name="ymax">最大Y值</param>
    public Envelope(double xmin, double xmax, double ymin, double ymax)
    {
        XMin = xmin;
        XMax = xmax;
        YMin = ymin;
        YMax = ymax;
    }
    
    /// <summary>
    /// 获取中心点
    /// </summary>
    public Point getCenter()
    {
    	return new Point((XMax + XMin)/ 2.0, (YMax + YMin) / 2.0);
    	
    }
    
    /// <summary>
    /// 获取宽度
    /// </summary>
    public double getWidth()
    {
    	return XMax - XMin;
    }
    
    /// <summary>
    /// 获取高度
    /// </summary>
    public double getHeight()
    {
    	return YMax - YMin;
    }
}
