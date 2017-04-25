function doRestCall(method, resturl) {
    $('#admin_subhead').text('');
    $.ajax(
        {
            type : method,
            url : resturl,
            timeout: 600000,
            success : function(result) {
                if (result.message) {
                    resultHtmlMessage = result.message;
                }
                else resultHtmlMessage ='';
                if (result.success) {
                    if (result.data)
                        resultHtmlData = '<pre style="font-size:10px">' + JSON.stringify(result.data, undefined, 2) + '</pre>';
                    else
                        resultHtmlData = '<pre style="white-space:nowrap">Success</pre>';
                }
                else {
                    if (result.data)
                        resultHtmlData = '<pre class="alert alert-danger">'+result.data+'</pre>';
                    else
                        resultHtmlData = '';
                }

                showPopupDialog(resultHtmlMessage, resultHtmlData);
            },
            error : function(result) {
                showPopupDialog("Server Error", result.responseText);
            }
        });

    return false;
}

function doRestCallWithReload(method, resturl) {
    $('#admin_subhead').text('');
    $.ajax(
        {
            type : method,
            url : resturl,
            timeout: 600000,
            success : function(result) {
                if (result.message) {
                    resultHtmlMessage = result.message;
                }
                else resultHtmlMessage ='';
                if (result.success) {
                    if (result.data)
                        resultHtmlData = '<pre style="font-size:10px">' + JSON.stringify(result.data, undefined, 2) + '</pre>';
                    else
                        resultHtmlData = '<pre style="white-space:nowrap">Success</pre>';
                }
                else {
                    if (result.data)
                        resultHtmlData = '<pre class="alert alert-danger">'+result.data+'</pre>';
                    else
                        resultHtmlData = '';
                }

                window.location.reload(true);
            },
            error : function(result) {
                showPopupDialog("Server Error", result.responseText);
            }
        });

    return false;
}

function showPopupDialog(title, message) {
    $('#rest_call_result_title').html(title);
    $('#rest_call_result_message').html(message);
    $('#rest_call_result').show();
}

function getObjectById(dboname, dboid) {
    if (!dboid) {
        $('#rest_call_result').html("<div class='alert alert-danger'><strong>Error!</strong> Valid id is required.</div>");
    }
    else {
        doRestCall('GET', '../v1/admin/DboAdmin?object_name='+dboname+'&object_id='+dboid);
    }
    return false;
}

function deleteObjectById(dboname, dboid) {
    if (!dboid) {
        $('#rest_call_result').html("<div class='alert alert-danger'><strong>Error!</strong> Valid id is required.</div>");
    }
    else {
        doRestCall('DELETE', '../v1/admin/DboAdmin?object_name='+dboname+'&object_id='+dboid);
    }
    return false;
}
