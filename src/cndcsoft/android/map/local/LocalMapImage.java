package cndcsoft.android.map.local;

import cndcsoft.android.map.lib.MapImage;

public class LocalMapImage extends MapImage{

	@Override
	public void getData() {
		// TODO Auto-generated method stub
        if (this.Image == null){
            _service_ref.getimage(this);
        }
	}
	/// <summary>
    /// 地图服务关联
    /// </summary>
    LacalMapService _service_ref;

    LocalMapImage(){
    }
}
