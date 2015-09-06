/**
 * js验证
 * 
 */
$.extend($.fn.validatebox.defaults.rules, {  
	 // 手机号码  
	mobile : {
		 validator : function(value) {
			 return /^(13|15|18|17)[0-9]{9}$/i.test(value); 
		},
		message : "手机号码格式不正确"
	},
	
	// 电话号码  
	phone : {
	    validator : function(value) {  
	        return /^([0-9]{3,4}-)?[0-9]{7,8}$/i.test(value);  
	    },  
	    message : "格式不正确,请使用下面格式:010-88888888" 
	},
	
	// 密码
	password : {
		 validator : function(value) {
			 return /^\w{6,16}$/i.test(value); 
		},
		message : "密码长度为6-16个字符,只能包含数字,大小写字母及下划线"
	},

});
