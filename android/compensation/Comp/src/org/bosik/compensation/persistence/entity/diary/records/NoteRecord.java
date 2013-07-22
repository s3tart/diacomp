package org.bosik.compensation.persistence.entity.diary.records;

public class NoteRecord extends DiaryRecord
{
	private String text = "";

	public NoteRecord(int time, String value)
	{
		setTime(time);
		setText(value);
	}

	// ================================ ВАЛИДАТОРЫ ================================

	public static boolean check(String value)
	{
		return (value != null);
	}

	public static boolean check(int time, String value)
	{
		return checkTime(time) && check(value);
	}

	// ================================ GET / SET ================================

	public String getText()
	{
		return text;
	}

	public void setText(String value)
	{
		if (!check(value))
			throw new IllegalArgumentException("NoteRecord: неверное значение поля Text (" + value + ")");

		if (!this.text.equals(value))
		{
			this.text = value;
			notifyModified();
		}
	}

}