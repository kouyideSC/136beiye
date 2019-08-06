<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="card-header">
    <div class="planflom-header">
        <div class="card-header-title clearfix">
            <button type="button" class="card-close plus-icon p-guanbi"></button>
            <div class="pull-left">
                <h1 class="entry-name">
                    <span class="plus-icon"></span>
                    <span>赛事详细</span>
                </h1>
            </div>
        </div>
        <div class="card-header-con">
            <ul class="abstract-list clearfix">
                <li class="abstract-item">
                    <p class="abstract-label">期次</p>
                    <div class="abstract-value">
                        <span>${params.period}</span>
                    </div>
                </li>
                <li class="abstract-item">
                    <p class="abstract-label">赛事</p>
                    <div class="abstract-value">
                        <span>${params.leagueName}</span>
                    </div>
                </li>
                <li class="abstract-item">
                    <p class="abstract-label">主队</p>
                    <div class="abstract-value">
                        <span>${params.hostName}</span>
                    </div>
                </li>
                <li class="abstract-item">
                    <p class="abstract-label">客队</p>
                    <div class="abstract-value">
                        <span>${params.guestName}</span>
                    </div>
                </li>
                <li class="abstract-item">
                    <p class="abstract-label">比赛时间</p>
                    <div class="abstract-value">
                        <span>${params.matchTime}</span>
                    </div>
                </li>
                <li class="abstract-item">
                    <p class="abstract-label">score</p>
                    <div class="abstract-value">
                        <span>${params.period}</span>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="card-main">
    <ul class="mian-detail clearfix">
        <li class="information-basic">
            <div class="white-box">
                <div class="card-tab-list">
                    <ul class="card-tab-nav clearfix">
                        <li class="card-tab-item active">
                            <a href="javascript:;">基本信息</a>
                        </li>
                    </ul>
                </div>
                <div class="card-tab-con active">待填充</div>
            </div>
        </li>
        <li class="card-record">
            <div class="white-box">
                <div class="card-tab-list">
                    <ul class="card-tab-nav clearfix">
                        <li class="card-tab-item active">
                            <a href="javascript:;">其它信息</a>
                        </li>
                    </ul>
                </div>
                <div class="card-tab-con active">
                    <div class="tab-con-details">
                        待填充
                    </div>
                </div>
            </div>
        </li>
        <li class="related-information">
            <div class="white-box">
                <div class="card-tab-list">
                    <ul class="card-tab-nav clearfix">
                        <li class="card-tab-item active">
                            <a href="javascript:;">相关信息</a>
                        </li>
                    </ul>
                </div>
                <div class="card-tab-con active">
                    <div class="tab-con-details">
                        待填充
                    </div>
                </div>
            </div>
        </li>
    </ul>
</div>
<div class="card-wrap" id="dt_peratorModal"></div>