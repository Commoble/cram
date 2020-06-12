package com.github.commoble.cram.vanilla;

import com.github.commoble.cram.api.AutoCramPlugin;
import com.github.commoble.cram.api.CramPlugin;
import com.github.commoble.cram.api.CramRegistrator;

@AutoCramPlugin
public class VanillaCramPlugin implements CramPlugin
{

	@Override
	public void register(CramRegistrator registry)
	{
		System.out.println("Registered vanilla plugin!");
	}

}
