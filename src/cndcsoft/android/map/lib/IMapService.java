package cndcsoft.android.map.lib;

public interface IMapService {

	/// <summary>
    /// ��ͼ�����ʼ������
    /// </summary>
    /// <param name="paramlist">��ʼ������</param>
    void Initialize(Object[] paramlist);
    
    /// <summary>
    /// ��ȡ��ͼĬ�ϲ���
    /// </summary>
    /// <returns>���ص�ͼĬ�ϲ���</returns>
    MapParam getDefaultMapParam();
    /// <summary>
    /// ��ȡ��ͼ
    /// </summary>
    /// <param name="mapParameter">��ͼ����</param>
    /// <returns>���ص�ͼ����</returns>
    MapResult getMapImage(MapParam mapParameter);
    /// <summary>
    /// ��չ������ã�ֻ��Զ�̷�����ʵ����
    /// </summary>
    /// <param name="commName">��չ��������</param>
    /// <param name="paramlist">�������</param>
    /// <returns>����ؽ��</returns>
    String expandCommand(String commName, Object[] paramlist);
    /// <summary>
    /// �ͷ���Դ
    /// </summary>
    
    Level[] getLevel();

    void Dispose();
}
