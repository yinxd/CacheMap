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
    /// ��ͼ�������
    /// </summary>
    LacalMapService _service_ref;

    LocalMapImage(){
    }
}
