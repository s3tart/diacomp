package org.bosik.compensation.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.Context;
import android.util.Log;

public class FileWorker
{
	private static final String	TAG	= FileWorker.class.getSimpleName();
	private Context				context;

	public FileWorker(Context context)
	{
		this.context = context;
	}

	/**
	 * Проверяет, существует ли файл с указанным именем
	 * 
	 * @param fileName
	 *            Имя файла
	 * @return Существует ли файл
	 */
	public boolean fileExists(String fileName)
	{
		boolean result = context.getFileStreamPath(fileName).exists();

		/*
		 * if (result) Log.d(TAG, "File '" + fileName + "' exists"); else Log.d(TAG, "File '" +
		 * fileName + "' does not exist");
		 */

		return result;
	}

	/**
	 * Читает содержимое файла в строку
	 * 
	 * @param fileName
	 *            Имя файла
	 * @return Содержимое файла в виде строки
	 * @throws IOException
	 */
	public String readFromFile(String fileName) throws IOException
	{
		FileInputStream stream = context.openFileInput(fileName);
		InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null)
		{
			sb.append(line);
		}
		reader.close();

		Log.v(TAG, "Reading from file '" + fileName + "': " + sb.toString());
		return sb.toString();

	}

	/**
	 * Записывает строку в файл (исходное содержимое файла уничтожается).
	 * 
	 * @param fileName
	 *            Имя файла
	 * @param base
	 *            Строка
	 * @throws IOException
	 */
	public void writeToFile(String fileName, String data) throws IOException
	{
		Log.v(TAG, "Writing to file '" + fileName + "': " + data);

		FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		outputStream.write(data.getBytes());
		outputStream.close();
	}
}
