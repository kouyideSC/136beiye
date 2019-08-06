function getMonthFirstDayMiku(date, offset) {
    if (offset == undefined) {
        offset = 0;
    }
    var year = date.getFullYear();
    var month = date.getMonth() + offset;
    var year_offset = Math.floor(month / 12);
    year += year_offset;
    month = month - year_offset * 12 + 1;
    var temp = new Date(year + "/" + month + "/1");
    return temp;
}

function getMonthLastDayMiku(date, offset) {
    if (offset == undefined) {
        offset = 0;
    }
    var year = date.getFullYear();
    var month = date.getMonth() + offset + 1;
    var year_offset = Math.floor(month / 12);
    year += year_offset;
    month = month - year_offset * 12 + 1;
    var temp = new Date(year + "/" + month + "/1");
    temp.setDate(temp.getDate() - 1);
    return temp;
}

function getNextMonthToday(date) {
    var year = date.getFullYear();
    var month = date.getMonth();
    var day = date.getDate();

    month = month + 1;
    if (month > 11) {
        year++;
        month -= 12;
    }

    var temp = getMonthLastDayMiku(date, 1);

    if (temp.getDate() < day) {
        temp = new Date(year + "/" + (month + 1) + "/" + temp.getDate());
    } else {
        temp = new Date(year + "/" + (month + 1) + "/" + day);
    }
    temp.setDate(temp.getDate() - 1);
    return temp;
}

function formatDateMiku(date) {
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    return year + "-" + month + "-" + day;
}

function formatTimeMiku(dateStr) {
    dateStr = dateStr.replace(/-/g, "/");
    var index = dateStr.indexOf(".");
    dateStr = dateStr.substring(0, index);
    var date = new Date(dateStr);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes()
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minute < 10) {
        minute = "0" + minute;
    }
    return year + "-" + month + "-" + day + " " + hour + ":" + minute;
}

/**
 * 获取下个月的一号
 */
function getNextBeginDate(beginDate) {
    var year = beginDate.getFullYear();
    var month = beginDate.getMonth();
    month = month + 1;
    if (month > 11) {
        year++;
        month -= 12;
    }
    return new Date(year + "/" + (month + 1) + "/1");
}

/**
 * 获取上个月的最后一天
 */
function preEndDate(date) {
    date.setDate(1);
    var oneDay = 1000 * 60 * 60 * 24;
    return new Date(date - oneDay);
}

/**
 * 获取所属月份
 * 1、对于未跨月的服务周期，落在哪个月就算哪个月。
 * 2、对于跨月的服务周期，按计费结束日期判断，计费结束日期>16日的，算当月（计费结束日期所在月份），计费结束日期<=16日的，算上月（计费结束日期所在月份-1）。
 * 3、当按照1、2条件，却出现两个周期属于同一个月份时，则日期大的那个周期所属月份自动+1。
 * 人工修改规则：若人工修改出现多个周期属于同一个月份时，则无法保存，红框出来并给予错误提示：不允许多个服务周期属于同一个月份。
 */
function getBelongMonth(array, beginDate, endDate) {
    if (beginDate.Format('yyyy-MM') == endDate.Format('yyyy-MM')) { //未跨月
        // 如果上个月的所属月份是和这个一样 则自动加1
        return getThisBelongMonth(array,endDate);
    } else {//跨月
        if (endDate.getDate() < 17) { //属于上个月
            var preDate = preEndDate(endDate);
            return getThisBelongMonth(array, preDate);
        } else {
            return getThisBelongMonth(array,endDate);
        }
    }
}

/**
 * 校验上个月的金额所属月份是否和这次一样 一样则加1
 */
function getThisBelongMonth(array, date) {
    if (array != null && array != undefined && array.length != 0) {
        var belongMonth = array[array.length - 1].belongMonth;
        if (belongMonth != undefined && belongMonth != '' && belongMonth == date.Format('yyyy-MM')) {
            return getNextBeginDate(date).Format('yyyy-MM');
        }
    }
    return date.Format('yyyy-MM');
}


/**
 * 获取本月最后一天的日期
 */
function getEndDate(beginDate) {
    var currentMonth = beginDate.getMonth();
    var nextMonth = ++currentMonth;
    var nextMonthFirstDay = new Date(beginDate.getFullYear(), nextMonth, 1);
    var oneDay = 1000 * 60 * 60 * 24;
    return new Date(nextMonthFirstDay - oneDay);
}

/**
 * 获取当前月的第一天
 */
function getCurrentMonthFirst() {
    var date = new Date();
    date.setDate(1);
    return date;
}

/**
 * 获取当前月的最后一天
 */
function getCurrentMonthLast() {
    var date = new Date();
    var currentMonth = date.getMonth();
    var nextMonth = ++currentMonth;
    var nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1);
    var oneDay = 1000 * 60 * 60 * 24;
    return new Date(nextMonthFirstDay - oneDay);
}


Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}