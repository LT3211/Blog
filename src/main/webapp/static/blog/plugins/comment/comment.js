function getRootPath() {
    //获取当前网址，如： http://localhost:9527/zdss-web/login/login.do
    var curWwwPath = window.document.location.href;
    //   console.log("当前网址：" + curWwwPath);

    //获取主机地址之后的目录，如：zdss-web/login/login.do
    var pathName = window.document.location.pathname;
    //  console.log("当前路径：" + pathName);

    var pos = curWwwPath.indexOf(pathName);
    //   console.log("路径位置：" + pos);

    //获取主机地址，如： http://localhost:9527
    var localhostPath = curWwwPath.substring(0, pos);
    console.log("当前主机地址：" + localhostPath);

    //获取带"/"的项目名，如：/zdss-web
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    console.log("当前项目名称：" + projectName);

    return localhostPath+projectName;
}

$('#commentSubmit').click(function () {
    var blogId = $('#blogId').val();
    var commentator = $('#commentator').val();
    var email = $('#email').val();
    var websiteUrl = $('#websiteUrl').val();
    var commentBody = $('#commentBody').val();
    if (isNull(blogId)) {
        swal("参数异常", {
            icon: "warning",
        });
        return;
    }
    if (isNull(commentator)) {
        swal("请输入你的称呼", {
            icon: "warning",
        });
        return;
    }
    if (isNull(email)) {
        swal("请输入你的邮箱", {
            icon: "warning",
        });
        return;
    }
    if (!validCN_ENString2_100(commentator)) {
        swal("请输入符合规范的名称(不要输入特殊字符)", {
            icon: "warning",
        });
        return;
    }
    if (!validCN_ENString2_100(commentBody)) {
        swal("请输入符合规范的评论内容(不要输入特殊字符)", {
            icon: "warning",
        });
        return;
    }
    var data = {
        "blogId": blogId, "commentator": commentator,
        "email": email, "websiteUrl": websiteUrl, "commentBody": commentBody
    };
    console.log(data);
    $.ajax({
        type: 'POST',//方法类型
        url:  getRootPath() +'/blog/comment',
        data: data,
        success: function (result) {
            if (result.resultCode == 200) {
                swal("评论提交成功请等待博主审核", {
                    icon: "success",
                });
                $('#commentBody').val('');
            }
            else {
                swal(result.data, {
                    icon: "error",
                });
            }
            ;
        },
        error: function () {
            swal("操作失败", {
                icon: "error",
            });
        }
    });
});