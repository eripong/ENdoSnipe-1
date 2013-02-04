package jp.co.acroquest.endosnipe.perfdoctor;

/**
 * WarningUnitを作成するためのクラス。
 * @author fujii
 *
 */
public class WarningUnitGetter
{
    /**
     * WarningUnitを作成する。
     * @param unitId 警告のID
     * @param id ルールのID
     * @param description 警告の説明。
     * @param className クラス名。
     * @param methodName メソッド名。
     * @param level 重要度
     * @param logFileName ログファイル名。
     * @param logFileLineNumber 行番号。
     * @param args 閾値、検出値などの引数。
     * @return WarnignUnit WarningUnit
     */
    public static WarningUnit createWarningUnit(String unitId, String id, String description,
            String className, String methodName, String level, String logFileName,
            int logFileLineNumber, long startTime, long endTime, Object[] args)
    {
        return new WarningUnit(unitId, id, description, className, methodName, level, logFileName,
                               logFileLineNumber, startTime, endTime, true, args);
    }

    /**
     * WarningUnitを作成する。
     * @param unitId 警告のID
     * @param id ルールのID
     * @param description 警告の説明。
     * @param className クラス名。
     * @param methodName メソッド名。
     * @param level 重要度
     * @param logFileName ログファイル名。
     * @param logFileLineNumber 行番号。
     * @param isEvent イベントであるかどうか。
     * @param stackTrace スタックトレース
     * @param args 閾値、検出値などの引数。
     * @return WarnignUnit WarningUnit
     */
    public static WarningUnit createWarningUnit(String unitId, String id, String description,
            String className, String methodName, String level, String logFileName,
            int logFileLineNumber, long startTime, long endTime, boolean isEvent,
            String stackTrace, Object[] args)
    {
        return new WarningUnit(unitId, id, description, className, methodName, level, logFileName,
                               logFileLineNumber, startTime, endTime, true, isEvent, stackTrace,
                               args);
    }

}
