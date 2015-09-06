	function newItem(){
        $('#dlg').dialog('open').dialog('setTitle','添加患者');
        $('#fm').form('clear');
        $("#password").removeAttr("readonly");
        $("#confPwd").removeAttr("readonly");
        $("#mobile").removeAttr("readonly");        
        var url = App.ctx + "/erp/patient/patientSave";            
        $("#url").val(url);     
        $("#bornYear").val("");     
        $("#bornMonth").val("");     
        
    }         
    function saveItem(){
    	var url = $("#url").val();
        $('#fm').form('submit',{
            url: url,
            onSubmit: function(){
                return $(this).form('validate');
            },  
            success: function(result){  
                var result = eval('('+result+')');
                if (result.code && result.code > 0){
                	if (result.msg) {
                		errorMsg(result.msg);
                	}
                } else {  
                    $('#dlg').dialog('close');
                    loadData2(result);
                }  
            }  
        });  
    }
    
    $(function(){
    	loadData(1, 20);
    	var pg1 = $('#dg').datagrid("getPager");
    	if (pg1) {
    		$(pg1).pagination({
    			onSelectPage : function(pn, pageSize) {
    				$("#pn").val(pn);
    				$("#num").val(pageSize);
    				loadData(pn, pageSize);
    			}
    		});
    	}
    	$("#bornYear").val('1980');
    });
    function setData(json){
    	$("#dg").datagrid('loadData',JSON.parse(json));
    }        
    function loadData(page, size) {
    	var name = $('#name').val();
    	var phone = $('#phone').val();
    	var surl = App.ctx + "/erp/patient/patientSearch";
    	var param = "pn="+page+"&pageSize="+size+"&name="+name;
    	param += "&phone=" + phone;
    	dealAjaxOpration(surl, param, "setData", false);
    }
    function loadData2(data) {
    	var dt;
    	if (typeof data == "string") {
    		dt = JSON.parse(data);
    	} else {
    		dt = data;
    	}
    	loadData(dt.pageNo, dt.pageSize);
    }
    function closeDiag() {
    	$('#dlgConf').dialog('close');
    }
    
    function editItem(){
        var row = $('#dg').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','编辑专家');
            $('#fm').form('load',row);
            var url = App.ctx + "/erp/patient/patientUpdate";            
            $("#url").val(url);              
            //设置任意密码，并将密码只读
            $("#password").val("2222222");
            $("#confPwd").val("2222222");
            $("#password").attr("readonly","readonly");
            $("#confPwd").attr("readonly","readonly");
            $("#mobile").attr("readonly","readonly");
            
        } else {
       		alertMsg("请先选择患者信息");
        }
    }
    /**
     * 默认选中
     * @param objId  selectbox id
     * @param objVal 选中值
     */
    function defaultSelect(objId,objVal) {
    	$("#" +objId).val("1980");
    }
    