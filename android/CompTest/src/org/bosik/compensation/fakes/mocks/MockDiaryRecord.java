package org.bosik.compensation.fakes.mocks;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.bosik.compensation.bo.FoodMassed;
import org.bosik.compensation.bo.diary.DiaryRecord;
import org.bosik.compensation.bo.diary.records.BloodRecord;
import org.bosik.compensation.bo.diary.records.InsRecord;
import org.bosik.compensation.bo.diary.records.MealRecord;
import org.bosik.compensation.bo.diary.records.NoteRecord;
import org.bosik.compensation.utills.TestUtils;

public class MockDiaryRecord extends TestCase implements Mock<DiaryRecord>
{
	private Mock<FoodMassed>	mockFoodMassed	= new MockFoodMassed();

	public List<DiaryRecord> getSamples()
	{
		List<DiaryRecord> samples = new ArrayList<DiaryRecord>();

		samples.add(new BloodRecord(16, 7.1, 0));
		samples.add(new BloodRecord(17, 7.0, 1));
		samples.add(new BloodRecord(160, 5.2, 2));

		samples.add(new InsRecord(1439, 16.0));

		MealRecord meal1 = new MealRecord(201, false);
		meal1.add(new FoodMassed("�������� \"���������\" (����)", 9.9, 26.3, 0, 276, 90));
		meal1.add(new FoodMassed("���� ������ \"�������\"", 5.5, 0.9, 44.1, 206.3, 42));
		samples.add(meal1);

		MealRecord meal2 = new MealRecord(200, true);
		meal2.add(new FoodMassed("�����", 0.0, 0.0, 99.8, 379.0, 6.0));
		samples.add(meal2);

		MealRecord meal3 = new MealRecord(200, true);
		for (FoodMassed f : mockFoodMassed.getSamples())
		{
			meal3.add(f);
		}
		samples.add(meal3);

		samples.add(new MealRecord(215, true));

		samples.add(new NoteRecord(680, "Just a �������� record with \"quotes\""));

		samples.add(new NoteRecord(681, ""));

		return samples;
	}

	public void compare(DiaryRecord exp, DiaryRecord act)
	{
		assertEquals(exp.getTime(), act.getTime());
		assertEquals(exp.getClass(), act.getClass());

		// @formatter:off
		if (exp.getClass() == BloodRecord.class)	compareBloodRecords((BloodRecord) exp, (BloodRecord) act); else
		if (exp.getClass() == InsRecord.class)		compareInsRecords((InsRecord) exp, (InsRecord) act); else
		if (exp.getClass() == MealRecord.class)		compareMealRecords((MealRecord) exp, (MealRecord) act); else
		if (exp.getClass() == NoteRecord.class)		compareNoteRecords((NoteRecord) exp, (NoteRecord) act);
		// @formatter:on
	}

	private void compareBloodRecords(BloodRecord exp, BloodRecord act)
	{
		assertEquals(exp.getValue(), act.getValue(), TestUtils.EPS);
		assertEquals(exp.getFinger(), act.getFinger());
	}

	private void compareInsRecords(InsRecord exp, InsRecord act)
	{
		assertEquals(exp.getValue(), act.getValue(), TestUtils.EPS);
	}

	private void compareMealRecords(MealRecord exp, MealRecord act)
	{
		assertEquals(exp.getTime(), act.getTime());
		assertEquals(exp.getShortMeal(), act.getShortMeal());
		assertEquals(exp.count(), act.count());

		for (int j = 0; j < exp.count(); j++)
		{
			mockFoodMassed.compare(exp.get(j), act.get(j));
		}
	}

	private static void compareNoteRecords(NoteRecord expRecord, NoteRecord actRecord)
	{
		assertEquals(expRecord.getText(), actRecord.getText());
	}
}