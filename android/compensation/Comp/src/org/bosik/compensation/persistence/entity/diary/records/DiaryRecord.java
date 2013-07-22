package org.bosik.compensation.persistence.entity.diary.records;

import org.bosik.compensation.persistence.entity.diary.DiaryChangeListener;
import org.bosik.compensation.persistence.entity.diary.DiaryChangeListener.EventType;
import org.bosik.compensation.utils.Utils;

/* ЗАМЕТКИ 
 * 
 * Порядок создания класса:
 * 		1. Создать все необходимые private-поля.
 * 		2. Создать public static-методы вида checkField(value), реализующие валидацию.
 * 		3. Создать get-методы.
 * 		4. Создать set-методы, проводящие валидацию check-методом.
 * 				4.0. Провести валидацию. Если провалилась, выбросить исключение IllegalArgumentException
 * 				4.1. Если нет — проверить, что новое значение отличается от старого 
 * 				4.2. Если отличается — вызвать notifyModified();
 * 		5. Создать валидатор, проверяющий сразу все поля (????????)
 * 		6. Создать конструктор, принимающий сразу все поля и устанавливающий их с помощью set-методов.
 * 
 * Порядок использования:
 * 		1. Провести валидацию check-методом (да, при этом валидация будет проведена дважды, ну и что?)
 * 		2. Изменить значение с помощью set-метода.
 */

public abstract class DiaryRecord
{
	@SuppressWarnings("unused")
	private static final String TAG = "DiaryRecord";

	// данные
	private int time;
	private DiaryChangeListener changeListener = null;

	// служебные
	// TODO: remove silent mode here, leave this feature just at DiaryPage level
	private boolean silentMode = true;
	private boolean modified = false;

	// ============================== СЛУЖЕБНЫЕ МЕТОДЫ ==============================

	protected void notifyModified()
	{
		// пытаемся оповестить страницу и опустить флаг
		if ((changeListener != null) && (!silentMode))
		{
			// Log.i(TAG, "notifyModified(): notifying the changeListener");
			changeListener.changed(EventType.MODIFY, this.getClass(), this);
			modified = false;
		} else
		{
			// запоминаем непереданное изменение и поднимаем флаг модифицированности
			modified = true;

			/*
			 * if (silentMode) Log.v(TAG, "notifyModified(): silentMode is on"); else Log.v(TAG,
			 * "notifyModified(): parent changeListener == null");
			 */
		}
	}

	public void beginUpdate()
	{
		silentMode = true;
	}

	public void endUpdate()
	{
		silentMode = false;
		if (modified)
		{
			notifyModified();
		}
	}

	// ================================ ВАЛИДАТОРЫ ================================

	public static boolean checkTime(int time)
	{
		return (time >= 0) && (time < Utils.SecPerDay);
	}

	// ================================ GET / SET ================================

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		if (!checkTime(time))
			throw new IllegalArgumentException("DiaryRecord: неверное значение поля Time (" + time + ")");

		if (time != this.time)
		{
			this.time = time;
			notifyModified();
		}
	}

	public DiaryChangeListener getChangeListener()
	{
		return changeListener;
	}

	public void setChangeListener(DiaryChangeListener changeListener)
	{
		this.changeListener = changeListener;
	}
}