package org.bosik.compensation.persistence.sync;

import org.bosik.compensation.persistence.repository.common.BaseRepository;
import android.util.Log;

public class SyncBaseRepository<T>
{
	private static final String TAG = SyncBaseRepository.class.getSimpleName();
	
	public void synchronize(BaseRepository<T> source1, BaseRepository<T> source2)
	{
		int version1 = source1.getVersion();
		int version2 = source2.getVersion();

		Log.i(TAG, "version1 = " + version1);
		Log.i(TAG, "version2 = " + version2);
		
		if (version1 > version2)
			source2.postBase(source1.getBase());
		else
			if (version1 < version2)
				source1.postBase(source2.getBase());
	}
}
