/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuangweizong.opencv;

public class Constant {

	public final static String TAG = "opencvProduct";

	public class PAYMENT{
		public static final String PAYMENT_WEIXIN = "wx8869708c4c66b636";
	}


	public class UIDesign {
		// 标准UI设计宽度（px�?
		public static final float UI_DESIGN_WIDTH = 720.0f;
		public static final float UI_DESIGN_HEIGHT = 1280.0f;
	}
	
	public class PAYMENT_STATU{
		public static final int PAYMENT_STATU_NO = 1;
		public static final int PAYMENT_STATU_YES = 2;
		public static final int PAYMENT_STATU_CANCEL = 3;
		public static final int PAYMENT_STATU_BACKING = 4;
		public static final int PAYMENT_STATU_BACKFINISH = 5;
		public static final int PAYMENT_STATU_BACKAPPLY = 14;
		
	}



	public class TYPE {
		public static final int TYPE_HOUSE = 14;
		public static final int TYPE_CARPOSITION = 18;
	}

	public class URL {
		/* 用户模块 */

		public static final String BASE_URL = "http://60.255.157.2:8080/"; // 基本路径
		public static final String IMAGE_BASE_URL = "http://60.255.157.2:8080/getImage/";


	}

}
