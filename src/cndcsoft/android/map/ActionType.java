package cndcsoft.android.map;

public enum ActionType {
	  /// <summary>
    /// ƽ��
    /// </summary>
    Pan, 
    /// <summary>
    /// ����Ŵ�
    /// </summary>
    ZoomInByClick,  
    /// <summary>
    /// �����С
    /// </summary>
    ZoomOutByClick,
 
    /// <summary>
    /// ����Ŵ󣨴���չ��
    /// </summary>
    ZoomInByRect,

    /// <summary>
    /// ������С������չ��
    /// </summary>
    ZoomOutByRect,

    /// <summary>
    /// �ҵ�λ��(GPS/LBS-����չ)
    /// </summary>
    LocationMe

}
