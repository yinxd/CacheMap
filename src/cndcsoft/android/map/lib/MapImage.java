package cndcsoft.android.map.lib;

import android.graphics.Bitmap;

public abstract class MapImage {
	  /// <summary>
    /// ��Ļ����Xֵ
    /// </summary>
    public int left;
    /// <summary>
    /// ��Ļ����Yֵ
    /// </summary>
    public int top;
    /// <summary>
    /// ͼƬ��С���ȿ�ߣ�
    /// </summary>
    public int size; 
    /// <summary>
    /// ��ƬID
    /// </summary>
    public String id;
    /// <summary>
    /// ��Ƭ�Ƿ�Ϊ��
    /// </summary>
    public boolean isSpacer;
    /// <summary>
    /// ��ƬͼƬ
    /// </summary>
    //public Image Image = null;
    public Bitmap Image=null;

    /// <summary>
    /// ��ȡ��ͼ��Ƭ������Ҫ���̳�
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
