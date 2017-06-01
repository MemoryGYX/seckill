//存放主要交互逻辑js代码
// javascript 模块化
// seckill.detail.init(params)
var seckill ={
    //封装秒杀相关AJAX url
    URL : {
        now : function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        excution : function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/excution';
        }
    },
    //处理秒杀逻辑
    handlerSeckill:function (seckillId,node) {
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //在回掉函数中执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5']
                    var killUrl = seckill.URL.excution(seckillId,md5);
                    console.log('killUrl'+ killUrl);
                    //绑定一次点击事件，
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求的操作
                        $(this).addClass('disabled');//禁用按钮
                        $.post(killUrl,{},function (result) {
                            var killResult = result['data'];
                            var state = killResult['state'];
                            var stateInfo = killResult['stateInfo'];
                            if(result && result['success']){
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }else{
                                node.html('<span class="label label-danger">'+stateInfo+'</span>');
                            }
                        })
                    });
                    node.show();
                }else{
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计时
                    seckill.countdown(seckillId,now,start,end);
                }
            }else{
                console.log(result);
            }
        })
    },
    //验证手机号
    validatePhone:function (phone) {
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },
    countdown:function (seckillId,nowTime,startTime,endTime) {
        var seckillBox = $('#seckill-box');
        //时间判断
        if(nowTime > endTime){
            seckillBox.html('秒杀结束');
        }else if(nowTime < startTime){
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function (event) {
                var format = event.strftime('秒杀计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () {
                seckill.handlerSeckill(seckillId,seckillBox);
            });
        }else{
            seckill.handlerSeckill(seckillId,seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail : {
        //详情页初始化
        init : function(params){
            //手机验证和登陆，计时交互
            //规划交互流程
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            if(!seckill.validatePhone(killPhone)) {
                //绑定Phone
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'})
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label laben-danger">手机号错误</label>').show(300);
                    }
                });
            }
            //已经登录
            //记时交互
            $.get(seckill.URL.now(),{},function(result){
                if(result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId,nowTime,startTime,endTime);
                }else{
                    cosole.log('result :'+result);
                }
            });
        }
    }
}