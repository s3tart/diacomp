package org.bosik.diacomp.fakes.mocks;

import java.util.List;

public interface Mock<T>
{
	List<T> getSamples();

	void compare(T exp, T act);
}