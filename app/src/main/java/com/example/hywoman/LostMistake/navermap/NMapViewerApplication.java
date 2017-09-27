/*
 * NMapViewerApplication.java $version 2010. 12. 16
 *
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.hywoman.LostMistake.navermap;

import android.app.Application;

/**
 * @author SeJin Lee 
 */
public class NMapViewerApplication extends Application {

	private static NMapViewerApplication instance;

	public static NMapViewerApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		instance = this;
	}
}
