/*  
 *  Diacomp - Diabetes analysis & management system
 *  Copyright (C) 2013 Nikita Bosik
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.bosik.diacomp.android.frontend.activities;

import java.io.Serializable;
import org.bosik.merklesync.HashUtils;
import org.bosik.merklesync.Versioned;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class ActivityEditor<T extends Serializable> extends Activity
{
	// private static final String TAG = ActivityEditor.class.getSimpleName();

	// TODO: rename to FIELD_CREATE_MODE
	public static final String	FIELD_MODE		= "bosik.pack.createMode";
	public static final String	FIELD_ENTITY	= "bosik.pack.entity";

	/**
	 * Stores the data for editing. Note: as far as passing this data achieved via
	 * serialization/deserialization, the editor's entity is completely unlinked (deeply cloned)
	 * from invoker's one. Thus, feel free to modify it the way you like.
	 */
	protected Versioned<T>		entity;

	/* =========================== MAIN METHODS ================================ */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setupInterface();
		readEntity(getIntent());
		boolean createMode = getIntent().getBooleanExtra(FIELD_MODE, true);
		showValuesInGUI(createMode);

		if (createMode)
		{
			entity.setId(HashUtils.generateGuid());
		}
	}

	/* =========================== PROTECTED METHODS ================================ */

	/**
	 * Read entity from Intent
	 * 
	 * @param intent
	 */
	@SuppressWarnings("unchecked")
	protected void readEntity(Intent intent)
	{
		entity = (Versioned<T>) intent.getExtras().getSerializable(FIELD_ENTITY);
	}

	/**
	 * Write entity to Intent
	 * 
	 * @param intent
	 */
	protected void writeEntity(Intent intent)
	{
		intent.putExtra(ActivityEditor.FIELD_ENTITY, entity);
	}

	/**
	 * Call this method on "Save" button click
	 */
	protected void submit()
	{
		if (getValuesFromGUI())
		{
			entity.updateTimeStamp();

			Intent intent = getIntent();
			writeEntity(intent);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/* =========================== ABSTRACT METHODS ================================ */

	/**
	 * The subject
	 */
	protected abstract void setupInterface();

	/**
	 * Show data in GUI
	 */
	protected abstract void showValuesInGUI(boolean createMode);

	/**
	 * Read and validate data from GUI
	 * 
	 * @return True if validation succeed, false otherwise
	 */
	protected abstract boolean getValuesFromGUI();
}
