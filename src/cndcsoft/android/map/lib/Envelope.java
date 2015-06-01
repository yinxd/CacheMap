package cndcsoft.android.map.lib;


/**
 * @author xiaox
 * 
 *
 */
public class Envelope {

	///��СXֵ
    public double XMin;

    /// ��СYֵ
    public double YMin;

    /// ���Xֵ
    public double XMax;

    /// ���Yֵ
    public double YMax;
    
    /// <summary>
    /// ���캯��
    /// </summary>
    /// <param name="xmin">��СXֵ</param>
    /// <param name="xmax">���Xֵ</param>
    /// <param name="ymin">��СYֵ</param>
    /// <param name="ymax">���Yֵ</param>
    public Envelope(double xmin, double xmax, double ymin, double ymax)
    {
        XMin = xmin;
        XMax = xmax;
        YMin = ymin;
        YMax = ymax;
    }
    
    /// <summary>
    /// ��ȡ���ĵ�
    /// </summary>
    public Point getCenter()
    {
    	return new Point((XMax + XMin)/ 2.0, (YMax + YMin) / 2.0);
    	
    }
    
    /// <summary>
    /// ��ȡ���
    /// </summary>
    public double getWidth()
    {
    	return XMax - XMin;
    }
    
    /// <summary>
    /// ��ȡ�߶�
    /// </summary>
    public double getHeight()
    {
    	return YMax - YMin;
    }
}
