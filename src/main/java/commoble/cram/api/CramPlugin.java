package com.github.commoble.cram.api;

import com.github.commoble.cram.api.functions.CramRegistrator;

public interface CramPlugin
{
	public void register(CramRegistrator registry);
}
