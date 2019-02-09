var seckill = {
    URL: {
        now: '/seckill/time/now',
        exposer: function (seckillId) {
            return "/seckill/" + seckillId + "/exposer";
        }
    },
    // 验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone))
            return true;
        return false;
    },
    detail: {
        init: function (params) {
            var killPhone = GetCookieValue("killPhone");
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //如果没登录，弹窗
                $('#killPhoneModal').modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //添加输入的电话号码为cookie
                        // document.cookie = "killPhone=" + inputPhone;
                        // $.cookie("killPhone",inputPhone);
                        /**
                         * 下面这么设置后端的CookieValue才能获取到...什么鬼...
                         */
                        $.cookie("killPhone",inputPhone,{expires:7,path:'/seckill'});
                        //刷新当前界面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            //到这里就已经注册了电话号码，可以执行秒杀了
            $.get(seckill.URL.now, {}, function (result) {
                if (result && result["code"]) {
                    var nowTime = result["data"];
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    //没有获取到当前时间则输出result方便调试
                    console.log(result);
                }
            })
        }
    },
    // 倒计时
    countdown: function (seckillId, nowTime, startTime, endTime) {
        console.log(seckillId + "," + nowTime + "," + startTime + "," + endTime);

        var seckillBox = $("#seckill-box");
        if (nowTime > endTime) {
            console.log(1);
            seckillBox.html("秒杀已经结束");
        } else if (nowTime < startTime) {
            console.log(2);
            //秒杀还没开始，显示倒计时
            //防止用户端时间偏移，所以加1s
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (e) {
                //格式化,先格式化strftime,再倒计时countdown
                var format = e.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ');
                seckillBox.html(format);
                // seckillBox.html(killTime);
            }).on("finish.countdown", function () {
                //倒计时结束后执行的方法(回调事件)
                console.log("倒计时结束，开始秒杀");
                // 秒杀开始后显示按钮，暴露秒杀路径
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {
            console.log(3);
            //秒杀开始
            // 秒杀开始后显示按钮，暴露秒杀路径
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },
    handleSeckill: function (seckillId, node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        //执行这一步是避免某些客户端时间走得过快，与服务器的时间不同，所以要判断服务器时间是否到开启秒杀
        $.post(
            seckill.URL.exposer(seckillId),
            {},
            function (result) {
                if (result && result['code']) {
                    var exposer = result['data'];
                    if (exposer['exposed']) {
                        //允许秒杀
                        node.show();
                        //url:{seckillId}/{md5}/execution
                        var md5 = exposer['md5'];
                        //暴漏秒杀地址~~
                        var killUrl = '/seckill/' + seckillId + "/" + md5 + "/execution";
                        console.log("url:" + killUrl);
                        //接着把地址绑定到button事件
                        $('#killBtn').click(function () {
                                $(this).addClass("disabled");
                                //真正秒杀操作
                                $.post(
                                    killUrl,
                                    {},
                                    function (result) {
                                        //如果秒杀成功并写入数据库
                                        if(result && result['code']){
                                            var execution = result['data'];
                                            var state = execution['state'];
                                            var stateInfo = execution['stateInfo'];
                                            node.html("<span class='label label-success'>"+stateInfo+"</span>");
                                        }else {
                                            console.log("秒杀结果?"+result);
                                            var message = result['message']
                                            node.html("<span class='label label-success'>"+message+"</span>");
                                        }
                                    }
                                );
                            }
                        )
                    } else {
                        //未开启秒杀，提取秒杀商品时间和nowTime再执行一遍倒计时
                        var startTime = exposer['start'];
                        var endTime = exposer['end'];
                        var nowTime = exposer['now'];
                        //再倒计时一遍，由于每个电脑计时有快慢
                        seckill.countdown(seckillId, nowTime, startTime, endTime);
                    }
                } else {
                    //异常
                    console.log("result:" + result);
                }
            }
        );
    }
}

function GetCookieValue(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie != '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = jQuery.trim(cookies[i]);
            if (cookie.substring(0, name.length + 1) == (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}