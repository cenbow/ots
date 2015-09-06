/**
 * ajax调用函数 function  dealAjaxOpration(urlpara,datapara,callbackName,async)
 * urlpara:ajax调用方式的URL 返回值为纯文本
 * datapara:上送数据  例如：para=1&para1=2
 * callbackName:回调函数，具体实现自己的数据解析
 * async:false为同步 true为异步
 * event:触发时间的Event
 */
function dealAjaxOpration(urlpara,datapara,callbackName,async)
{
	$.ajax({
		   type: 'POST',
		   url: urlpara, //请求的action
		   data:  datapara, //传的参数
		   dataType: 'text',
		   async: async,
		   success: function (data){ //结果
			   data = data.Trim();
		       var cbn = callbackName+"(data)";
		       eval(cbn);
		   },	       
		   error:function(data){
			   alert("Appear Error.");
		   }
	});
}
