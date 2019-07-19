define([], function () {
        ruckus.pubsub = {
                'channels': {
                        'contests': '/ws/contests',
                        'contestliveoverview': '/ws/contestliveoverview',
                        'contestlive': '/ws/contestlive'
                },
                'subscriptions': {
                        'sockets': {
                                'contests': {
                                        'all': 'socket.CONTESTS_ALL',
                                        'add': 'socket.CONTEST_ADD',
                                        'update': 'socket.CONTEST_UPDATE',
                                        'remove': 'socket.CONTEST_REMOVE'
                                },
                                'contestliveoverview': {
                                        'all': 'socket.CONTESTLIVEOVERVIEW_ALL',
                                        'update': 'socket.CONTESTLIVEOVERVIEW_UPDATE'
                                },
                                'contestlivedetail': {
                                        'all': 'socket.CONTESTLIVEDETAIL_ALL',
                                        'projectiongraph': 'socket.CONTESTLIVEPROJECTIONGRAPH_ALL',
                                        'entry': {
                                                'update': 'socket.CONTESTLIVEDETAIL_ENTRY_UPDATE'
                                        },
                                        'sportevent': {
                                                'update': 'socket.CONTESTLIVEDETAIL_SPORTEVENT_UPDATE'
                                        },
                                        'athlete': {
                                                'sporteventinfoupdate': 'socket.CONTESTLIVEDETAIL_ATHLETESPORTEVENTINFO_UPDATE',
                                                'statusupdate': 'socket.CONTESTLIVEDETAIL_INDICATOR_UPDATE'
                                        }
                                }
                        },
                        'models': {
                                'data': {
                                        'contests': {
                                                'all': 'model.contest.all',
                                                'add': 'model.contest.add',
                                                'update': 'model.contest.update',
                                                'remove': 'model.contest.remove'
                                        },
                                        'contestevents': {
                                                'all': 'model.contestevents.retrieve'
                                        },
                                        'contestliveoverview': {
                                                'all': 'model.contestliveoverview.all',
                                                'update': 'model.contestliveoverview.update'
                                        },
                                        'contestlivedetail': {
                                                'all': 'model.contestlivedetail.all',
                                                'projectiongraph': {
                                                        'all': 'model.contestlivedetail.projectiongraph.all'
                                                },
                                                'entry': {
                                                        'update': 'model.contestlivedetail.entry.update'
                                                },
                                                'sportevent': {
                                                        'update': 'model.contestlivedetail.sportevent.update'
                                                },
                                                'athlete': {
                                                        'sporteventinfoupdate': 'model.contestlivedetail.athlete.update',
                                                        'statusupdate': 'model.contestlivedetail.indicator.update'
                                                }
                                        },
                                        'contestscoring': {
                                                'update': 'model.contestscoring.retrieve'
                                        },
                                        "timestamp": {
                                                "servertime": "model.timestamp.servertime"
                                        }
                                }
                        },
                        'view': {
                                'lobby': {
                                        'contestresults': {
                                                'pageload': 'view.contestresults.pageload',
                                                'contests': {
                                                        'all': 'view.contestresults.contest.all',
                                                        'add': 'view.contestresults.contest.add',
                                                        'update': 'view.contestresults.contest.update',
                                                        'remove': 'view.contestresults.contest.remove'
                                                },
                                                'contestscoring': {
                                                        'update': 'view.contestresults.contestscoring.retrieve'
                                                },
                                                "servertime": "view.contestresults.timestamp.servertime"
                                        },
                                        'contestresultinfo': {
                                                'pageload': 'view.lobby.contestresultinfo.pageload',
                                                'events': {
                                                        'all': 'view.lobby.contestresultinfo.all'
                                                }
                                        }
                                },
                                'contestentry': {
                                        'contestrow': {
                                                "servertime": "view.contestentry.timestamp.servertime"
                                        }
                                },
                                'dashboard': {
                                        'servertime': 'view.dashboard.servertime',
                                        'contest': {
                                                'all': 'view.dashboard.contest.all',
                                                'get': {
                                                        'all': 'view.dashboard.contest.get.all',
                                                        'byId': 'view.dashboard.contest.get.byId'
                                                },
                                                'active': {
                                                        'currentpayout': 'view.dashboard.contest.active.currentpayout',
                                                        'currentposition': 'view.dashboard.contest.active.currentposition',
                                                        'currentpoints': 'view.dashboard.contest.active.currentpoints',
                                                        'currentunitsremaining': 'view.dashboard.contest.active.currentunitsremaining',
                                                        'projectedpayout': 'view.dashboard.contest.active.projectedpayout'
                                                }
                                        },
                                        'athlete': {
                                                'all': {
                                                        'request': 'view.dashboard.athlete.all.request',
                                                        'response': 'view.dashboard.athlete.all.response'
                                                },
                                                'byId': {
                                                        'request': 'view.dashboard.athlete.byid.request',
                                                        'response': 'view.dashboard.athlete.byid.response'
                                                }
                                        }
                                },
                                'dashboardcontest': {
                                        'servertime': 'view.dashboardentry.servertime',
                                        'pageload': 'view.dashboardentry.pageload',
                                        'contest': {
                                                'entry': {
                                                        'all': 'view.dashboardentry.entry.all',
                                                        'fpp': 'view.dashboardentry.entry.fpp',
                                                        'statsupdate': 'view.dashboardentry.athletesporteventinfo.statsupdate',
                                                        'unitsremaining': 'view.dashboardentry.entry.unitsremaining'
                                                },
                                                'athlete': {
                                                        'statsupdate': 'view.dashboardentry.athletesporteventinfo.statusupdate',
                                                        'timeline': 'view.dashboardentry.athletesporteventinfo.timeline',
                                                        'unitsremaining': 'view.dashboardentry.athletesporteventinfo.unitsremaining'
                                                },
                                                'sportevent': {
                                                        'update': 'view.dashboardentry.sportevent.update'
                                                },
                                                'indicator': {
                                                        'update': 'view.dashboardentry.indicator.update'
                                                },
                                                'projectiongraph': {
                                                        'all': 'view.dashboardentry.projectiongraph.all'
                                                }
                                        },
                                        'get': {
                                                'allathletes': 'view.dashboardentry.request.athletes.all',
                                                'athlete': 'view.dashboardentry.request.athletes.update',
                                                'allentries': 'view.dashboardentry.request.entries.all',
                                                'entry': 'view.dashboardentry.request.entries.update',
                                                'allsportevents': 'view.dashboardentry.request.sportevents.all',
                                                'sportevent': 'view.dashboardentry.request.sportevents.update'
                                        },
                                        'put': {
                                                'contest': 'view.dashboardentry.submit.contest',
                                                'allathletes': 'view.dashboardentry.submit.athletes',
                                                'athlete': 'view.dashboardentry.submit.athlete',
                                                'allentries': 'view.dashboardentry.submit.entries',
                                                'entry': 'view.dashboardentry.submit.entry',
                                                'allsportevents': 'view.dashboardentry.submit.sportevents',
                                                'sportevent': 'view.dashboardentry.submit.sportevent'
                                        }
                                },
                                'dashboardathletes': {
                                        'servertime': 'view.dashboardathletes.servertime',
                                        'pageload': 'view.dashboardathletes.pageload',
                                        'contest': {
                                                'entry': {
                                                        'all': 'view.dashboardathletes.entry.all',
                                                        'fpp': 'view.dashboardathletes.entry.fpp',
                                                        'statsupdate': 'view.dashboardathletes.athletesporteventinfo.statsupdate',
                                                        'unitsremaining': 'view.dashboardathletes.entry.unitsremaining'
                                                },
                                                'athlete': {
                                                        'statsupdate': 'view.dashboardathletes.athletesporteventinfo.statusupdate',
                                                        'timeline': 'view.dashboardathletes.athletesporteventinfo.timeline',
                                                        'unitsremaining': 'view.dashboardathletes.athletesporteventinfo.unitsremaining'
                                                },
                                                'sportevent': {
                                                        'update': 'view.dashboardathletes.sportevent.update'
                                                },
                                                'indicator': {
                                                        'update': 'view.dashboardathletes.indicator.update'
                                                },
                                                'projectiongraph': {
                                                        'all': 'view.dashboardathletes.projectiongraph.all'
                                                }
                                        },
                                        'get': {
                                                'allathletes': 'view.dashboardathletes.request.athletes.all',
                                                'athlete': 'view.dashboardathletes.request.athletes.update',
                                                'allentries': 'view.dashboardathletes.request.entries.all',
                                                'entry': 'view.dashboardathletes.request.entries.update',
                                                'allsportevents': 'view.dashboardathletes.request.sportevents.all',
                                                'sportevent': 'view.dashboardathletes.request.sportevents.update'
                                        },
                                        'put': {
                                                'contest': 'view.dashboardathletes.submit.contest',
                                                'allathletes': 'view.dashboardathletes.submit.athletes',
                                                'athlete': 'view.dashboardathletes.submit.athlete',
                                                'allentries': 'view.dashboardathletes.submit.entries',
                                                'entry': 'view.dashboardathletes.submit.entry',
                                                'allsportevents': 'view.dashboardathletes.submit.sportevents',
                                                'sportevent': 'view.dashboardathletes.submit.sportevent'
                                        }
                                }
                        }
                }
        };
});