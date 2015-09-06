//全选与取消全选多选下拉列表的方法 isSelect为0为取消全选 否则为全选
function multiSelectAll(name,isSelect,text){
	if(isSelect==0){
		$("input[name='"+name+"']").attr('checked', false).parent().removeClass('checked');
	}else{
		$("input[name='"+name+"']").attr('checked', true).parent().addClass('checked');
	}
	var txt = "";//用于存储显示批次文本框的文字
	$("input[name='"+name+"']").each(function(i){
	if($(this).parent()[0].style.display=="none"){
	$(this).parent().attr("class","");
	$(this).attr("checked",false);
	}
	if($(this).parent().attr("class")=="checked"){
	var pici = $(this).parent().html().substring($(this).parent().html().indexOf(">")+1);
	txt = txt+pici+", ";
	}
	});
	$("input[name='"+name+"']").parent().parent().prev().children(".multiSelect_txt").val(txt==""?text:txt);
}

//输入站点名称，模糊查询到相关的站点并在多选下拉列表中选中
function selectallnexusbranch(path,tagname,branchname){
	if(branchname.length>0){
		$.ajax({
			url:path+"/datastatistics/selectallnexusbranch",//后台处理程序
			type:"POST",//数据发送方式 
			data:"branchname="+branchname,//参数
			dataType:'json',//接受数据格式
			success:function(data){
				for(var j = 0 ; j < data.length ; j++){
					var txt = "";//用于存储显示批次文本框的文字
					$(".multiSelectOptions input[name='"+tagname+"']").each(function(){
						if($(this).val()==data[j].branchid){
							$(this).attr('checked', true).parent().addClass('checked');
						}
						if($(this).parent()[0].style.display=="none"){
							$(this).parent().attr("class","");
							$(this).attr("checked",false);
						}
						if($(this).parent().attr("class")=="checked"){
							var pici = $(this).parent().html().substring($(this).parent().html().indexOf(">")+1);
							txt = txt+pici+", ";
						}
					});
					$("input[name='"+tagname+"']").parent().parent().prev().children(".multiSelect_txt").val(txt==""?"请选择":txt);
				}
			}
		});
	}
}


function selectjingquebranch(path,tagname,branchname){
	if(branchname.length>0){
		$.ajax({
			url:path+"/datastatistics/selectnexusbranchbybranchname",//后台处理程序
			type:"POST",//数据发送方式 
			data:"branchname="+branchname,//参数
			dataType:'json',//接受数据格式
			success:function(data){
				var txt = "";//用于存储显示批次文本框的文字
				$(".multiSelectOptions input[name='"+tagname+"']").each(function(){
					if($(this).val()==data.branchid){
						$(this).attr('checked', true).parent().addClass('checked');
					}
					if($(this).parent()[0].style.display=="none"){
						$(this).parent().attr("class","");
						$(this).attr("checked",false);
					}
					if($(this).parent().attr("class")=="checked"){
						var pici = $(this).parent().html().substring($(this).parent().html().indexOf(">")+1);
						txt = txt+pici+", ";
					}
				});
				$("input[name='"+tagname+"']").parent().parent().prev().children(".multiSelect_txt").val(txt==""?"请选择":txt);
			}
		});
	}
}


//输入站点名称，精确查询到相关的站点并在非多选下拉列表中选中
function selectnotallnexusbranch(path,tagname,branchname){
	if(branchname.length>0){
		$.ajax({
			url:path+"/datastatistics/selectnexusbranchbybranchname",//后台处理程序
			type:"POST",//数据发送方式 
			data:"branchname="+branchname,//参数
			dataType:'json',//接受数据格式
			success:function(data){
				$("#"+tagname+" option:selected").removeAttr("selected");
				$("#"+tagname+" option[value="+data.branchid+"]").attr("selected","selected");
			}
		});
	}
}
//输入站点名称  查询机构
function selectbranchbybranchname(path,tagname,branchname){
	if(branchname.length>0){
		$.ajax({
			url:path+"/datastatistics/selectbranchbybranchname",//后台处理程序
			type:"POST",//数据发送方式 
			data:"branchname="+branchname,//参数
			dataType:'json',//接受数据格式
			success:function(data){
				$("#"+tagname+" option:selected").removeAttr("selected");
				$("#"+tagname+" option[value="+data.branchid+"]").attr("selected","selected");
			}
		});
	}
}

//根据参数判断是精确查询还是模糊查询
function moHuOrJingQueSlect(ismohu,path,tagname,branchname){
	if(ismohu==1){
		selectallnexusbranch(path,tagname,branchname);
	}else{
		selectjingquebranch(path,tagname,branchname);
	}
}