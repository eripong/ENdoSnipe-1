package jp.co.acroquest.endosnipe.perfdoctor.classifier;

import static junit.framework.Assert.assertEquals;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnitGetter;

/**
 * 分類器のUtilクラス
 * @author fujii
 *
 */
public class ClassifierUtil
{
    /**　警告のID　*/
    public static final String UNIT_ID = "testWarningId";

    /** ルールのID */
    public static String ID = "testRuleId";

    /** 警告の説明 */
    public static final String DESCRIPTION = "This is a testWarningUtnit";

    /** クラス名 */
    public static final String CLASS_NAME = "testRuleId";

    /** メソッド名 */
    public static final String METHOD_NAME = "testRuleId";

    /** 重要度 */
    public static final String LEVEL = "ERROR";

    /** ログファイル名 */
    public static final String LOG_FILENAME = "file1";

    /** 行番号 */
    public static final int LOG_FILELINENUMBER = 1;

    /** 開始時刻 */
    public static final int STARTTIME = 0;

    /** 終了時刻 */
    public static final int ENDTIME = 0;

    /**
     * @param  。
     * @param  。
     * @param args 閾値、検出値などの引数。
     * @return　WarnignUnit WarningUnit
     */

    /**
     * 2つのWarnigUnitが一致するか検証する。
     * @param expect 期待するWarningUnit
     * @param result 結果のWarningUnit
     */
    public static void assertWarningUnitList(WarningUnit expect, WarningUnit result)
    {
        assertEquals(expect.getId(), result.getId());
        assertEquals(expect.getClassName(), result.getClassName());
        assertEquals(expect.getMethodName(), result.getMethodName());
        assertEquals(expect.getLevel(), result.getLevel());
        assertEquals(expect.getUnitId(), result.getUnitId());
        assertEquals(expect.getDescription(), result.getDescription());
        assertEquals(expect.getLogFileName(), result.getLogFileName());
        assertEquals(expect.getLogFileLineNumber(), result.getLogFileLineNumber());
        assertEquals(expect.getStartTime(), result.getStartTime());
        assertEquals(expect.getEndTime(), result.getEndTime());

        Object[] expectArgs = expect.getArgs();
        Object[] resultArgs = result.getArgs();
        assertEquals(expectArgs.length, resultArgs.length);
        for (int cnt = 0; cnt < expectArgs.length; cnt++)
        {
            assertEquals(expectArgs[cnt], resultArgs[cnt]);
        }
    }

    /**
     * 試験で共通的に利用するWarningUnitを作成する。
     * @param args WarningUnitのArgs
     * @return WarningUnit
     */
    public static WarningUnit createDefaultWarningUnit(Object[] args)
    {
        return WarningUnitGetter.createWarningUnit(UNIT_ID, ID, DESCRIPTION, CLASS_NAME,
                                                   METHOD_NAME, LEVEL, LOG_FILENAME,
                                                   LOG_FILELINENUMBER, STARTTIME, ENDTIME, args);
    }

    /**
     * 試験で共通的に利用するWarningUnitを作成する(イベント用)。
     * @param args WarningUnitのArgs
     * @return WarningUnit
     */
    public static WarningUnit createDefaultEventWarningUnit(String stackTrace, Object[] args)
    {
        return WarningUnitGetter.createWarningUnit(UNIT_ID, ID, DESCRIPTION, CLASS_NAME,
                                                   METHOD_NAME, LEVEL, LOG_FILENAME,
                                                   LOG_FILELINENUMBER, STARTTIME, ENDTIME, true,
                                                   stackTrace, args);
    }

    /**
     * 試験で共通的に利用するWarningUnitを作成する。
     * @param ruleId 警告のルールID
     * @param fileName ファイル名
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @param args WarningUnitのArgs
     * @return WarningUnit
     */
    public static WarningUnit createWarningUnit(String ruleId, String fileName, long startTime,
            long endTime, Object[] args)
    {
        return WarningUnitGetter.createWarningUnit(UNIT_ID, ruleId, DESCRIPTION, CLASS_NAME,
                                                   METHOD_NAME, LEVEL, fileName,
                                                   LOG_FILELINENUMBER, startTime, endTime, args);
    }

}
