package org.bosik.compensation.bo.common;

import java.text.DecimalFormat;
import java.text.ParseException;
import org.bosik.compensation.persistence.common.Unique;
import org.bosik.compensation.utils.Utils;

/**
 * Stores food's name, relative parameters (PFCV on 100g) and mass. Has methods for serialization /
 * deserialization.
 * 
 * @author Bosik
 * 
 */
public class FoodMassed extends Food
{
	private static final DecimalFormat	df			= new DecimalFormat("###.#");
	private static final char			FOOD_SEP	= '|';

	private double						mass;

	public FoodMassed()
	{

	}

	public FoodMassed(String name, double relProts, double relFats, double relCarbs, double relValue, double mass)
	{
		super(name, relProts, relFats, relCarbs, relValue);
		this.mass = mass;
	}

	// ================================ GET / SET ================================

	public double getMass()
	{
		return mass;
	}

	public void setMass(double mass)
	{
		checkNonNegativeThrowable(mass);
		this.mass = mass;
	}

	public double getProts()
	{
		return (getRelProts() / 100) * mass;
	}

	public double getFats()
	{
		return (getRelFats() / 100) * mass;
	}

	public double getCarbs()
	{
		return (getRelCarbs() / 100) * mass;
	}

	public double getValue()
	{
		return (getRelValue() / 100) * mass;
	}

	// ================================ CLONE ================================

	@Override
	public Unique clone() throws CloneNotSupportedException
	{
		FoodMassed result = (FoodMassed) super.clone();

		result.setMass(getMass());

		return result;
	}

	// ================================ I / O ================================

	// TODO: move these methods to serializers package

	/**
	 * Читает из текстового представления FoodMassed
	 * 
	 * @param s
	 *            Строка
	 * @throws ParseException
	 */
	public void read(String s) throws ParseException
	{
		String[] t = s.split("[\\[" + FOOD_SEP + "\\]:]+"); // БОЯН :D

		if (t.length != 6)
		{
			throw new IllegalArgumentException("Incorrect FoodMassed format: " + s);
		}

		// setters are used for additional validating
		setName(t[0]);
		setRelProts(Utils.parseDouble(t[1]));
		setRelFats(Utils.parseDouble(t[2]));
		setRelCarbs(Utils.parseDouble(t[3]));
		setRelValue(Utils.parseDouble(t[4]));
		setMass(Utils.parseDouble(t[5]));
	}

	/**
	 * Creates text representation
	 * 
	 * @return Строка
	 */
	public String write()
	{
		// TODO: use String.format()
		return getName() + '[' + df.format(getRelProts()) + FOOD_SEP + df.format(getRelFats()) + FOOD_SEP
				+ df.format(getRelCarbs()) + FOOD_SEP + df.format(getRelValue()) + "]:" + df.format(mass);
	}

	@Override
	public String toString()
	{
		return getName() + " (" + df.format(mass) + ")";
	}
}