var halook = {};

halook.ID = {}
halook.ID.MEASUREMENT_TIME = "measurementTime";
halook.ID.MEASUREMENT_VALUE = "measurementValue";
halook.ID.MEASUREMENT_ITEM_NAME = "measurementItemName";

halook.hbase = {};
halook.hbase.parent = {};
halook.hbase.dualslider = {};
halook.hbase.dualslider.UNIT = 60 * 60 * 1000;

halook.DATE_FORMAT_DETAIL = 'yyyy/MM/dd HH:mm:ss.fff';
halook.DATE_FORMAT_DAY = 'yyyy/MM/dd';
halook.DATE_FORMAT_HOUR = 'yyyy/MM/dd HH:mm';

halook.task = {};
halook.task.SUCCESSED = "SUCCEEDED";
halook.task.FAILED = "FAILED";
halook.task.KILLED = "KILLED";
halook.task.COMMIT_PENDING = "COMMIT_PENDING";
halook.task.RUNNING = "running";

halook.HDFS = {};
halook.HDFS.MESURE_TERM = 13000;

halook.hdfs = {};
halook.hdfs.constants = {};

halook.hdfs.constants.cycle = 5000;

halook.hdfs.constants.bgColor = "#303232";

halook.hdfs.constants.mainCircle = {};
halook.hdfs.constants.mainCircle.radius = 140;
halook.hdfs.constants.mainCircle.innerRate = 0.2;
halook.hdfs.constants.mainCircle.transferLineColor = "#EEEEEE";

halook.hdfs.constants.dataNode = {};
halook.hdfs.constants.dataNode.maxWidth = 60;
halook.hdfs.constants.dataNode.maxLength = 130;
halook.hdfs.constants.dataNode.frameColor = "rgba(255,255,255,0.5)";
halook.hdfs.constants.dataNode.color = {
	good : "#0C80A0",
	full : "#F09B4A",
	dead : "#AE1E2F"
};

halook.hdfs.constants.blockTransfer = {};
halook.hdfs.constants.blockTransfer.width = 4;
halook.hdfs.constants.blockTransfer.colorThreshold = 0.9;

halook.hdfs.constants.rack = {};
halook.hdfs.constants.rack.height = 10;
halook.hdfs.constants.rack.colors = [ "#666666", "#AAAAAA", "#CCCCCC" ];
// //////////////////////////////////////////////////////////
// option end
// //////////////////////////////////////////////////////////
halook.hdfs.constants.dataNode.status = {};
halook.hdfs.constants.dataNode.status.good = 0;
halook.hdfs.constants.dataNode.status.full = 1;
halook.hdfs.constants.dataNode.status.dead = 2;
halook.hdfs.constants.cycleInterval = 2000;
halook.hdfs.constants.hostnameAll = "--all--";

wgp.constants.STATE.SUCCESS = "success";
wgp.constants.STATE.RUNNING = "running";

wgp.constants.STATE.FAIL = "fail";
wgp.constants.STATE.FAILED = "fail";
wgp.constants.STATE.KILLED = "killed";
wgp.constants.STATE.MNORMAL = "mnormal";
wgp.constants.STATE.MRUNNING = "mrun";
wgp.constants.STATE.MFAIL = "mfail";
wgp.constants.STATE.MKILLED = "mkilled";
wgp.constants.STATE.RNORMAL = "rnormal";
wgp.constants.STATE.RRUNNING = "rrun";
wgp.constants.STATE.RFAIL = "rfail";
wgp.constants.STATE.RKILLED = "rkilled";
wgp.constants.STATE.TASKKILLED = "killed";
wgp.constants.STATE.TASKFAIL = "fail";

wgp.constants.JOB_STATE = {};
wgp.constants.JOB_STATE.NORMAL = "NORMAL";
wgp.constants.JOB_STATE.RUNNING = "RUNNING";
wgp.constants.JOB_STATE.FAIL = "FAILED";
wgp.constants.JOB_STATE.FAILED = "FAILED";
wgp.constants.JOB_STATE.KILLED = "KILLED";
wgp.constants.JOB_STATE.KILLED_UNCLEAN = "KILLED_UNCLEAN";
wgp.constants.JOB_STATE.SUCCESS = "SUCCESS";

wgp.constants.JOB_STATE_COLOR = {};
wgp.constants.JOB_STATE_COLOR.NORMAL = "#00FF00";
wgp.constants.JOB_STATE_COLOR.RUNNING = "#007700";
wgp.constants.JOB_STATE_COLOR.FAIL = "#FF0000";
wgp.constants.JOB_STATE_COLOR.KILLED = "#888800";
wgp.constants.JOB_STATE_COLOR.SUCCESS = "#0000FF";

// wgp.constants.STATE_COLOR = {};
// wgp.constants.STATE_COLOR[wgp.constants.STATE.NORMAL]="#00FF00";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.SUCCESS]="#00FF00";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.RUNNING]="#0000FF";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.KILLED]="#FFFF00";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.FAIL]="#FF0000";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.MNORMAL]="#007700";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.MRUNNING]="#00FF00";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.MFAIL]="#FF7700";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.MKILLED]="#777700";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.RNORMAL]="#000077";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.RRUNNING]="#0000FF";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.RFAIL]="#FF0077";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.RKILLED]="#770077";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.TASKEFAIL]="#FF0000";
// wgp.constants.STATE_COLOR[wgp.constants.STATE.TASKKILLED]="#FF7700";

wgp.constants.STATE_COLOR[wgp.constants.STATE.SUCCESS] = "#00FF00";
wgp.constants.STATE_COLOR[wgp.constants.STATE.RUNNING] = "#0000FF";
wgp.constants.STATE_COLOR[wgp.constants.STATE.KILLED] = "#FF6600";
wgp.constants.STATE_COLOR[wgp.constants.STATE.FAIL] = "#FF6600";
wgp.constants.STATE_COLOR[wgp.constants.STATE.FAILED] = "#FF6600";
wgp.constants.STATE_COLOR[wgp.constants.STATE.MNORMAL] = "#008000";
wgp.constants.STATE_COLOR[wgp.constants.STATE.MRUNNING] = "#00FF00";
wgp.constants.STATE_COLOR[wgp.constants.STATE.MFAIL] = "#FF0000";
wgp.constants.STATE_COLOR[wgp.constants.STATE.MKILLED] = "#777777";
wgp.constants.STATE_COLOR[wgp.constants.STATE.RNORMAL] = "#0000FF";
wgp.constants.STATE_COLOR[wgp.constants.STATE.RRUNNING] = "#0000FF";
wgp.constants.STATE_COLOR[wgp.constants.STATE.RFAIL] = "#C400C4";
wgp.constants.STATE_COLOR[wgp.constants.STATE.RKILLED] = "#777777";
wgp.constants.STATE_COLOR[wgp.constants.STATE.TASKEFAIL] = "#FF6600";
wgp.constants.STATE_COLOR[wgp.constants.STATE.TASKKILLED] = "#FF6600";

var ENdoSnipe = {};