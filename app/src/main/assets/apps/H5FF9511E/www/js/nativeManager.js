/**
 * 原生平台操作管理器
 */
var nativeManager = {
	/**
	 * 原生平台管理器
	 */
	manager: {
		/**
		 * iOS平台
		 */
		iOS: {
			/**
			 * 通知管理中心
			 */
			get notificationCenter() { 
				return plus.ios.importClass("NSNotificationCenter").defaultCenter()
			}
		},
		/**
		 * Android平台
		 */
		android: {
			/**
			 * 通知管理中心
			 */
			get notificationCenter() {
				return plus.android.importClass("com.example.andyyuan.androidmui.OpenActivityUtils")
			},
			/**
			 *  通信类名称
			 */
			communicationClassName: "com.example.andyyuan.androidmui.OpenActivityUtils",
			/**
             *  通信类名称+回调
             */
            communicationClassNameCallBack: "com.example.andyyuan.androidmui.OpenActivityUtils$CallBack",
			/**
			 *  通信类标准方法
			 */
			communicationDefaultFunctionName: "loadDataFromH5ToNative",
			/**
			 *  通信类回调标准方法
			 */
			callBackDefaultFunctionName: "loadDataFromNativeToH5",
			/**
			 *  通信类回调标准方法
			 */
			callBackEvent: function (callBack) {
				var _self = nativeManager.manager.android
				var callBackDefaultFunctionName = _self.callBackDefaultFunctionName
				var communicationClassNameCallBack = _self.communicationClassNameCallBack
				return plus.android.implements("com.example.andyyuan.androidmui.OpenActivityUtils$CallBack", {
					"loadDataFromNativeToH5" : function(jsonObject){
						callBack(jsonObject)
					}
				})
			},
			/**
			 * h5发送消息给原生
			 * @param {
			 * 			platform， 平台类型：Android
			 * 			methodType，方法类型：nativeManager.manager.methodType
			 * 			jsonObject, json数据对象
			 * 	} jsonObject
			 * @param {Object} callBack
			 */
			post: function (jsonObject, callBack) {
				try{
					if (plus) {
						var _self = nativeManager.manager.android
						plus.android.invoke(_self.communicationClassName, _self.communicationDefaultFunctionName, JSON.stringify(jsonObject), _self.callBackEvent(callBack))
					} else {
						console.log("H5与原生交互失败，请在原生项目中运行")
					}
				}catch(e){
					console.log("H5与原生交互失败，请在确定在原生项目中运行 error: "+e)
				}
			}
		},
		methodType: {
			/**
			 * 一般发送方法
			 */
			normal: "normal",
			/**
			 * 发送在线用户数据
			 */
			appUser: "appUser",
			/**
			 * 发送打开网格走访模块的指令
			 */
			openGridPatrol: "openGridPatrol",
			/**
			 * 发送 获取原生gps定位信息的指令
			 */
			loadGPSInfo: "loadGPSInfo",
			/**
			 * 发送打开图片IdOCR的模块指令
			 */
			openIdOCR:"openIdOCR",
			/**
			 * 发送选择地图上地理位置指令
			 */
			openLocationAddress:"openLocationAddress",
			/**
			 * 发送打开一对一视频
			 */
			openCRT:"openCRT"

		}
	},
	keys: {
		loadDataFromH5ToNative: "loadDataFromH5ToNative"	
	},
	/**
	 * 发送消息至原生APP
	 * @param {
	 * 			platform， 平台类型：iOS、Android
	 * 			methodType，方法类型：nativeManager.manager.methodType
	 * 			jsonObject, json数据对象
	 * 	} jsonObject
	 * @param {function(jsonObject)} callBack
	 */
	postMessage: function(jsonObject, callBack){
		
		var baseJsonObject = this.checkJSONObject(jsonObject)
		if (baseJsonObject.methodType == undefined || baseJsonObject.methodType == null) {
			baseJsonObject.methodType = this.manager.methodType.normal
		}
		if (mui.os.ios) {
			baseJsonObject.platform = "iOS"
			nativeManager.postMessageToIOS(baseJsonObject, callBack)
		} else {
			jsonObject.platform = "Android"
			nativeManager.postMessageToAndroid(baseJsonObject, callBack)
		}
		console.log("h5发送消息给原生APP："+JSON.stringify(baseJsonObject))
	},
	/**
	 * 向苹果原生APP发送数据
	 * @param {
	 * 			platform， 平台类型：iOS
	 * 			methodType，方法类型：nativeManager.manager.methodType
	 * 			jsonObject, json数据对象
	 * 	} jsonObject
	 * @param {function(jsonObject)} callBack
	 */
	postMessageToIOS: function(jsonObject, callBack){
		try{
			var manager = this.manager.iOS.notificationCenter
			if (manager) {
				
				manager.postNotificationNameobject(this.keys.loadDataFromH5ToNative, JSON.stringify(jsonObject))
			} else {
				console.log("H5与原生交互失败，请在原生项目中运行")
			}
		}catch(e){
			console.log("H5与原生交互失败，请在确定在原生项目中运行 error: "+e)
		}
	},
	/**
	 * 向安卓原生APP发送数据
	 * @param {
	 * 			platform， 平台类型：Android
	 * 			methodType，方法类型：nativeManager.manager.methodType
	 * 			jsonObject, json数据对象
	 * 	} jsonObject
	 * @param {function(jsonObject)} callBack
	 */
	postMessageToAndroid: function(jsonObject, callBack){
		this.manager.android.post(jsonObject, callBack)
	},
	/**
	 * 向原生APP发送在线用户数据
	 * @param {登录接口返回的JSON数据对象} jsonObject
	 */
	postAppUserMessage: function(jsonObject){
		var baseJsonObject = {
			"methodType" : this.manager.methodType.appUser
		}
		baseJsonObject.jsonObject = {
			"identity": jsonObject.user.id,
			"roleId": jsonObject.user.roleid,
			"deptId": jsonObject.user.deptid,
			"account": jsonObject.user.account,
			"password": jsonObject.user.password,
			"name": jsonObject.user.name,
			"avatarUrl": jsonObject.user.createtime,
			"createtime": jsonObject.user.account,
			"email": jsonObject.user.email,
			"phone": jsonObject.user.phone,
			"salt": jsonObject.user.salt,
			"sex": jsonObject.user.sex,
			"version": jsonObject.user.version,
			"status": jsonObject.user.status,
			"token": jsonObject.token,
			"baseUrl": common.constant.baseUrl,
			"uploadFileUrl": common.constant.uploadUrl
		}
		this.postMessage(baseJsonObject)
	},
	/**
	 * 向原生APP发送打开网格巡查模块的指令消息
	 */
	postOpenGridPatrolMessage: function(jsonObject){
		
		var baseJsonObject = {
			"methodType" : this.manager.methodType.openGridPatrol
		}
		baseJsonObject.jsonObject = {
			"identity": jsonObject.name,
			"token": jsonObject.age
		}
		this.postMessage(baseJsonObject)
	},
	/**
	 * 校验JSON对象
	 * @param {JSON对象} jsonObject
	 */
	checkJSONObject: function(jsonObject){
		if (jsonObject == undefined || jsonObject == null) {
			return {}
		}
		return jsonObject
	},
	
}