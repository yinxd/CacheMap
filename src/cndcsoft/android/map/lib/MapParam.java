package cndcsoft.android.map.lib;

public abstract class MapParam {
	 /// <summary>
    /// ��ͼ����
    /// </summary>
    public String MapName;
    /// <summary>
    /// ��ͼ��Χ
    /// </summary>
    public Envelope MapBound;
    /// <summary>
    /// ��ͼ��ǰ��Ұ��Χ
    /// </summary>
    public Envelope ViewBound;
    /// <summary>
    /// ��ͼ��ǰ������
    /// </summary>
    public double Scale;
    /// <summary>
    /// ��ͼ���ڿ��
    /// </summary>
    public int ClientWidth;
    /// <summary>
    /// ��ͼ���ڸ߶�
    /// </summary>
    public int ClientHeight;
    /// <summary>
    /// ��ͼ�Ƿ��ǻ���״̬
    /// </summary>
    public boolean IsCahced;

    // ��ʼ���ĵ�ͼ�������Ե�ͼ���ĵ�Ϊ���ģ��������߼���
    /// <summary>
    /// ȫͼ
    /// </summary>
    public void Entire(){

    }
    /// <summary>
    /// ���ݵ�ͼ��Ļ��Ŵ�
    /// </summary>
    /// <param name="scale"></param>
    /// <param name="x"></param>
    /// <param name="y"></param>
    public void Zoom(double scale,int x, int y)
    {

    }
    /// <summary>
    /// ���ݱ��������ĵ㶨λ
    /// </summary>
    /// <param name="scale"></param>
    /// <param name="center"></param>
    public void ZoomByScaleCenter(double scale, Point center)
    {

    }
    /// <summary>
    /// �����趨��ͼ�ؼ���С
    /// </summary>
    /// <param name="width"></param>
    /// <param name="height"></param>
    public void Resize(int width, int height)
    {

    }
    /// <summary>
    /// ƽ�Ʋ���
    /// </summary>
    /// <param name="dx"></param>
    /// <param name="dy"></param>
    public void Pan(int dx, int dy)
    {

    }
    /// <summary>
    /// ��ͼ����ת������Ļ����
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
    /// ��Ļ����ת������ͼ����
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
