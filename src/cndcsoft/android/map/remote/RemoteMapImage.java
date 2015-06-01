package cndcsoft.android.map.remote;

import cndcsoft.android.map.lib.MapImage;

public class RemoteMapImage extends MapImage{
	  /// <summary>
    /// 地图服务关联
    /// </summary>
    RemoteMapService _service_ref;

    RemoteMapImage(){
    }
    /// <summary>
    /// 获取切片图片
    /// </summary>
    @Override
    public void getData()
    {
        if (this.Image == null){
            _service_ref.getimage(this);
        }
    }
}
