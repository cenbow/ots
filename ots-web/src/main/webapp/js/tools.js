Date.prototype.Format = function(formatStr) {   
    var str = formatStr;   
    var Week = ['日','一','二','三','四','五','六'];  
    str=str.replace(/yyyy|YYYY/,this.getFullYear());   
    str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));   
    str=str.replace(/MM/,this.getMonth()>9?this.getMonth().toString():'0' + this.getMonth());   
    str=str.replace(/M/g,this.getMonth());   
    str=str.replace(/w|W/g,Week[this.getDay()]);   
    str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());   
    str=str.replace(/d|D/g,this.getDate());   
    str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());   
    str=str.replace(/h|H/g,this.getHours());   
    str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());   
    str=str.replace(/m/g,this.getMinutes());   
    str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());   
    str=str.replace(/s|S/g,this.getSeconds());   
    return str;   
}   
/**
 * 日期计算
 */
Date.prototype.DateAdd = function(strInterval, Number) { 
	var dtTmp = this; 
	switch (strInterval) { 
		case 's' :return new Date(Date.parse(dtTmp) + (1000 * Number)); 
		case 'n' :return new Date(Date.parse(dtTmp) + (60000 * Number)); 
		case 'h' :return new Date(Date.parse(dtTmp) + (3600000 * Number)); 
		case 'd' :return new Date(Date.parse(dtTmp) + (86400000 * Number)); 
		case 'w' :return new Date(Date.parse(dtTmp) + ((86400000 * 7) * Number)); 
		case 'q' :return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number*3, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds()); 
		case 'm' :return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds()); 
		case 'y' :return new Date((dtTmp.getFullYear() + Number), dtTmp.getMonth(), dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds()); 
	} 
};
/**
 * 取得当前日期所在月的最大天数
 */
Date.prototype.MaxDayOfDate = function() { 
	var myDate = this; 
	var ary = myDate.toArray(); 
	var date1 = (new Date(ary[0],ary[1]+1,1)); 
	var date2 = date1.dateAdd(1,'m',1); 
	var result = dateDiff(date1.Format('yyyy-MM-dd'),date2.Format('yyyy-MM-dd')); 
	return result; 
};
/**
 * 字符串转成日期类型
 * 格式 MM/dd/YYYY MM-dd-YYYY YYYY/MM/dd YYYY-MM-dd
 */
function StringToDate(DateStr) { 
	var converted = Date.parse(DateStr); 
	var myDate = new Date(converted); 
	if (isNaN(myDate)) { 
		var arys= DateStr.split('-'); 
		myDate = new Date(arys[0],--arys[1],arys[2]); 
	} 
	return myDate; 
} 
/**
 * 求两个时间的天数差 日期格式为 YYYY-MM-dd
 */
function daysBetween(DateOne,DateTwo) { 
	var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-')); 
	var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1); 
	var OneYear = DateOne.substring(0,DateOne.indexOf ('-')); 
	var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ('-')); 
	var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ('-')+1); 
	var TwoYear = DateTwo.substring(0,DateTwo.indexOf ('-')); 
	var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000); 
	return Math.abs(cha); 
}
/**
 * 文本Trim 
 */
String.prototype.Trim = function() { 
	return this.replace(/(^\s*)|(\s*$)/g, ""); 
};
String.prototype.LTrim = function() { 
	return this.replace(/(^\s*)/g, ""); 
};
String.prototype.Rtrim = function() { 
	return this.replace(/(\s*$)/g, ""); 
};
String.prototype.getBytesLength = function() { 
	return this.replace(/[^\x00-\xff]/gi, "--").length; 
};

/**
 * 数组判断对象是否存在 
 */
Array.prototype.contains = function(obj) {
	var i = this.length;
	while (i--) {
		if (this[i] == obj) {
			return true;
		}
	}
	return false;
};
/**
 * 数组删除一个对象后，返回新数组
 */
Array.prototype.remove = function(obj) {
	var i = 0;
	for (; i < this.length; i++) {
		if (this[i] == obj) {
			break;
		}
	}
	for (; i < this.length - 1; i++) {
		this[i] = this[i+1];
	}
	this.pop();
};

/**
 * 对数字进行格式化
 */
function numberFormat(num, decimalBit, includeSeparate) {
	var nn = parseFloat(num);
	if (includeSeparate == null) {
		includeSeparate = true;
	} 
	var str = nn.toFixed(decimalBit);
	var i = str.indexOf(".");
	if (includeSeparate && i > 3) {
	    var re=/\d{1,3}(?=(\d{3})+$)/g;      
        str=str.replace(/^(\d+)((\.\d+)?)$/,function(s,s1,s2){return s1.replace(re,"$&,")+s2;});
	}
	return str;
}
/**
 * 去除数字千位符
 * @param num
 */
function delNumSeparate(num) {
	return num.replace(/,/g, "");
}

function multiSelectUpdate(selector, vals) {
	var s = "";
	$(selector).each(function(i, o) {
		if (vals.contains($(o).val())) {
			$(o).attr("checked", true);
			s += "," + $(o).parent().text();
		} else {
			$(o).attr("checked", false);
		}
	});
	if (s != "") {
		$(".multiSelect_txt").val(s.substring(1));
	} else {
		$(".multiSelect_txt").val("多选项");
	}
	$('.multiSelectOptions').hide();
}

//信息展示
function alertMsg(msg) {
	$.messager.alert("提示", msg, "info");
}
function errorMsg(msg) {
	$.messager.alert("错误", msg, "error");
}
