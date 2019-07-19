<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-top">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#"></a>
        </div>
        <div class="collapse navbar-collapse" id="navbar-top">
                <ul class="nav navbar-nav">
                    <!--<li class="active" id="lobbylink"><a href="javascript:void(0);">Lobby</a></li> -->
                    <li id="gamesdropdownlink" class="dropdown">
                        <a href="#lobby">Lobby</a>
                        <!--class="dropdown-toggle" data-toggle="dropdown"
                        <ul class="dropdown-menu">
                            <li id="lobbylink"><a href="javascript:void(0);">DFS Lobby</a></li>
                            <li id="lobbyavsblink"><a href="javascript:void(0);">AvsB Lobby</a></li>
                        </ul>-->
                    </li>
                    <li id="dashboarddropdownlink" class="dropdown">
                            <a href="#dashboard" class="dropdown-toggle" data-toggle="dropdown">Dashboard</a>
                            <ul class="dropdown-menu">
                                    <li id="dashboardlink">
                                            <a href="javascript:void(0);">
                                                    <div class="col-xs-1 icons iconscontest_light dbddIcon"></div>
                                            Contests</a>
                                    </li>
                                    <li id="dashboardathleteslink">
                                            <a href="javascript:void(0);">
                                                    <div class="col-xs-1 icons iconsathletes_light dbddIcon"></div>
                                            Athletes</a>
                                    </li>
                                    <li id="dashboardlineupslink">
                                            <a href="javascript:void(0);">
                                                    <div class="col-xs-1 icons iconslineups_light dbddIcon"></div>
                                            Rosters</a>
                                    </li>
                                    <li id="dashboardhistorylink">
                                            <a href="javascript:void(0);">
                                                    <div class="col-xs-1 icons iconshistory_light dbddIcon"></div>
                                            History</a>
                                    </li>
                            </ul>
                    </li>
                    <!--<li id="examplepagesdropdownlink" class="dropdown">
                        <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown">Example Pages <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                                <li id="mainlink"><a href="javascript:void(0);">Main</a></li>
                                <li id="sectiononelink"><a href="javascript:void(0);">Section One</a></li>
                                <li id="sectiontwolink"><a href="javascript:void(0);">Section Two</a></li>
                                <li id="infolink"><a href="javascript:void(0);">HTML Controls</a></li>
                                <li id="sectiondatalink"><a href="javascript:void(0);">Data Access</a></li>
                        </ul>
                    </li>-->
                </ul>
                <ul class="nav navbar-nav navbar-right">
                        <li id="balancedepositlink">
                                <a href="">
                                        <div class="money" id="walletAmountUsd">{money}</div>
                                </a>
                        </li>
                        <li id="userdropdownlink" class="dropdown">
                                <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" style="width:132px;padding-left:0;padding-top:56px;">
                                        <div class="username ellipsis" id="application_username"></div>
                                </a>
                                <ul class="dropdown-menu">
                                        <li id="depositlink" class="noLink"><a href="javascript:void(0);">Deposit</a></li>
                                        <li id="withdrawllink" class="noLink"><a href="javascript:void(0);">Withdrawal</a></li>
                                        <li id="transactionhistorylink" class="noLink"><a href="javascript:void(0);">Transactions</a></li>
                                        <li class="divider"></li>
                                        <li id="referafriendlink" class="noLink"><a href="javascript:void(0);">Refer A Friend</a></li>
                                        <li id="loyaltybonuslink" class="noLink"><a href="javascript:void(0);">Loyalty Bonus</a></li>
                                        <li class="divider"></li>
                                        <li id="settingslink" class="noLink"><a href="javascript:void(0);">Settings</a></li>
                                        <li id="logoutlink"><a href="/auth/logout">Log Out</a></li>
                                </ul>
                        </li>
                </ul>
        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
</nav>
