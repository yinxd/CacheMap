package cndcsoft.android.map.remote;

import cndcsoft.android.map.lib.MapImage;

public class RemoteMapImage extends MapImage{
	  /// <summary>
    /// ��ͼ�������
    /// </summary>
    RemoteMapService _service_ref;

    RemoteMapImage(){
    }
    /// <summary>
    /// ��ȡ��ƬͼƬ
    /// </summary>
    @Override
    public void getData()
    {
        if (this.Image == null){
            _service_ref.getimage(this);
        }
    }
}
