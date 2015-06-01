package cndcsoft.android.map.lib;

public abstract class MapParam {
	 /// <summary>
    /// 地图名称
    /// </summary>
    public String MapName;
    /// <summary>
    /// 地图范围
    /// </summary>
    public Envelope MapBound;
    /// <summary>
    /// 地图当前视野范围
    /// </summary>
    public Envelope ViewBound;
    /// <summary>
    /// 地图当前比例尺
    /// </summary>
    public double Scale;
    /// <summary>
    /// 地图窗口宽度
    /// </summary>
    public int ClientWidth;
    /// <summary>
    /// 地图窗口高度
    /// </summary>
    public int ClientHeight;
    /// <summary>
    /// 地图是否是缓存状态
    /// </summary>
    public boolean IsCahced;

    // 初始化的地图参数，以地图中心点为中心，最大比例尺级别
    /// <summary>
    /// 全图
    /// </summary>
    public void Entire(){

    }
    /// <summary>
    /// 根据地图屏幕点放大
    /// </summary>
    /// <param name="scale"></param>
    /// <param name="x"></param>
    /// <param name="y"></param>
    public void Zoom(double scale,int x, int y)
    {

    }
    /// <summary>
    /// 根据比例尺中心点定位
    /// </summary>
    /// <param name="scale"></param>
    /// <param name="center"></param>
    public void ZoomByScaleCenter(double scale, Point center)
    {

    }
    /// <summary>
    /// 重新设定地图控件大小
    /// </summary>
    /// <param name="width"></param>
    /// <param name="height"></param>
    public void Resize(int width, int height)
    {

    }
    /// <summary>
    /// 平移操作
    /// </summary>
    /// <param name="dx"></param>
    /// <param name="dy"></param>
    public void Pan(int dx, int dy)
    {

    }
    /// <summary>
    /// 地图坐标转换到屏幕坐标
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    public int[] MapToClient(double x,double y){
        int[] ret = new int[2];
        //
        ret[0] = (int)(ClientWidth * (x - ViewBound.XMin) / (ViewBound.XMax - ViewBound.XMin));
        ret[1] = (int)(ClientHeight * (ViewBound.YMax - y) / (ViewBound.YMax - ViewBound.YMin));
        //
        return ret;
    }
    /// <summary>
    /// 屏幕坐标转换到地图坐标
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    public double[] ClientToMap(int x,int y){
        double[] ret = new double[2];
        //
        ret[0] = ViewBound.XMin + (ViewBound.XMax - ViewBound.XMin) * x / ClientWidth;
        ret[1] = ViewBound.YMax - (ViewBound.YMax - ViewBound.YMin) * y / ClientHeight;
        //
        return ret;
    }
}
