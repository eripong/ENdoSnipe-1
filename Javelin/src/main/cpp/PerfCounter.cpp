#include "jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter.h"
#include <conio.h>
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <pdh.h>	// Pdh.Lib
#include <tchar.h>	// tchar.h
#include <assert.h>	// assert()
#include <pdhmsg.h>	// PDH_MORE_DATA

#define	TactfulMalloc(a)	( (a) ? malloc(a) : 0 )
#define	TactfulFree(a)	if( a ){ free(a); a = NULL; }

/** カウンタ値取得に失敗した場合の戻り値 */
#define	DOUBLE_ERROR_VALUE	(0.0)

/** カウンタのインスタンス名の最大サイズ */
#define	INSTANCE_NAME_SIZE	(1024)

/** カウンタパス名の最大サイズ */
#define	COUNTER_PATH_SIZE	(1024)

/** GetIDProcess() でID Processが取得できなかった場合の戻り値 */
#define	INVALID_ID_PROCESS	(-1)

#define INITIALPATHSIZE 1000

PDH_HQUERY   hQuery;
PDH_HCOUNTER hCounterSysCPUSys;
PDH_HCOUNTER hCounterSysCPUUser;
PDH_HCOUNTER hCounterSysPageUsage;
PDH_HCOUNTER hCounterSysPageBytes;
PDH_HCOUNTER hCounterSysVirtualBytes;
PDH_HCOUNTER hCounterSysPageIn;
PDH_HCOUNTER hCounterSysPageOut;
PDH_HCOUNTER hCounterSysProcUser;
PDH_HCOUNTER hCounterSysProcSys;
PDH_HCOUNTER hCounterSysMajFlt;
PDH_HCOUNTER hCounterSysVSize;
PDH_HCOUNTER hCounterSysRSS;
PDH_HCOUNTER hCounterSysNumThreads;
PDH_HCOUNTER hCounterSysNumFDs;
PDH_HCOUNTER hCounterProcNumFDs;
TCHAR szCurrentInstanceName[INSTANCE_NAME_SIZE];




/****************************************************************************/
/*                    C++ 内部からのみ呼ばれる関数の定義                    */
/****************************************************************************/


/**
 * 指定されたカウンタの値を double 値で取得します。
 *
 * @param hCounter カウンタハンドル
 * @return 成功した場合はカウンタの値、失敗した場合は DOUBLE_ERROR_VALUE (-1)
 */
static double GetFormattedCounterValueByDouble(PDH_HCOUNTER hCounter)
{
	PDH_FMT_COUNTERVALUE fntValue;
	PDH_STATUS status = PdhGetFormattedCounterValue(
			hCounter, PDH_FMT_DOUBLE, NULL, &fntValue);
	if (status != ERROR_SUCCESS) 
	{
		fntValue.doubleValue = DOUBLE_ERROR_VALUE;
	}
	return fntValue.doubleValue;
}


/**
 * 指定されたカウンタの値を double 値で取得します。
 * 上限値を 100 で制限しません。
 *
 * @param hCounter カウンタハンドル
 * @return 成功した場合はカウンタの値、失敗した場合は DOUBLE_ERROR_VALUE (-1)
 */
static double GetFormattedCounterValueByDoubleNoCap100(PDH_HCOUNTER hCounter)
{
	PDH_FMT_COUNTERVALUE fntValue;
	PDH_STATUS status = PdhGetFormattedCounterValue(
			hCounter, PDH_FMT_DOUBLE | PDH_FMT_NOCAP100, NULL, &fntValue);
	if (status != ERROR_SUCCESS) 
	{
		fntValue.doubleValue = DOUBLE_ERROR_VALUE;
	}
	return fntValue.doubleValue;
}


/**
 * カウンターパスを作成する。
 *
 * @param pszInstanceName インスタンス名
 * @param pszCounterName カウンター名
 * @param pszCounterPath カウンターパス名格納先
 * @param dwCounterPathSize カウンターパス名格納可能サイズ
 * @return パスの作成に成功した場合は 1 、失敗した場合は -1
 */
static
bool MakeCounterPath(LPTSTR pszInstanceName, LPTSTR pszCounterName, LPTSTR pszCounterPath, DWORD dwCounterPathSize)
{
	PDH_COUNTER_PATH_ELEMENTS cpe;
	ZeroMemory( &cpe, sizeof(PDH_COUNTER_PATH_ELEMENTS) );

	cpe.szMachineName		= NULL; 
	cpe.szObjectName		= _T("Process");
	cpe.szInstanceName		= pszInstanceName;
	cpe.szParentInstance	= NULL;
	cpe.dwInstanceIndex		= -1;
	cpe.szCounterName		= pszCounterName;

	// カウンターパスの作成
	if( ERROR_SUCCESS != PdhMakeCounterPath( &cpe, pszCounterPath, &dwCounterPathSize, 0 ) )
	{
		return false;
	}

	return true;
}


/**
 * PDHカウンター値を取得します。
 *
 * @param pszInstanceName インスタンス名
 * @param pszCounterName パフォーマンスカウンタ名
 * @param dwFormat 取得する値のフォーマット（PDH_FMT_*）
 * @param pValue 値格納先
 * @param dwValueSize 値の格納可能サイズ
 * @param dwSleepMilliSecond 0 より大きな値を指定した場合、値を２回取得し、２回目の値を返す
 * @return 値を取得できた場合は true 、取得できなかった場合は false
 */
static
bool GetPdhCounterValue( LPTSTR pszInstanceName, LPTSTR pszCounterName, DWORD dwFormat, void* pValue, DWORD dwValueSize, DWORD dwSleepMilliSecond = 0 )
{
	// 入力チェック
	if( NULL == pszInstanceName
	 || NULL == pszCounterName
	 || NULL == pValue
	 || 0 == dwValueSize )
	{
		assert( !"入力エラー" );
		return false;
	}

	bool		bResult = false;		// 結果

	HQUERY		hQuery = NULL;		// 要求ハンドル
	HCOUNTER	hCounter = NULL;	// カウンターハンドル
	

  // カウンターパスの作成
	TCHAR szCounterPath[COUNTER_PATH_SIZE];
	if( !MakeCounterPath( pszInstanceName, pszCounterName, szCounterPath, COUNTER_PATH_SIZE ) )
	{
		goto LABEL_END;
	}

	// 要求ハンドルの作成
	if( ERROR_SUCCESS != PdhOpenQuery( NULL, 0, &hQuery ) )
	{
		goto LABEL_END;
	}

	// 作成したカウンターパスを要求ハンドルに登録。カウンターハンドルを得ておく。
	if( ERROR_SUCCESS != PdhAddCounter( hQuery, szCounterPath, 0, &hCounter ) )
	{
		goto LABEL_END;
	}

	// 要求データの取得
	DWORD dwErrorCode = PdhCollectQueryData( hQuery );
	if( ERROR_SUCCESS !=  dwErrorCode)
	{
		goto LABEL_END;
	}
	if( 0 < dwSleepMilliSecond )
	{
		Sleep( dwSleepMilliSecond );
		if( ERROR_SUCCESS != PdhCollectQueryData( hQuery ) )
		{
			goto LABEL_END;
		}
	}

	PDH_FMT_COUNTERVALUE	fmtValue;
	PdhGetFormattedCounterValue( hCounter, dwFormat, NULL, &fmtValue );

	bResult = true;

LABEL_END:
	PdhRemoveCounter( hCounter );
	PdhCloseQuery( hQuery );
	if( false == bResult )
	{	// 失敗
		return false;
	}

	// 成功
	switch( dwFormat )
	{
		case PDH_FMT_LONG:
			if( dwValueSize != sizeof(LONG) )
			{
				assert( !"受け取る値の型の種類に対して値受け取りバッファーのサイズが不正" );
				return false;
			}
			else
			{
				// pValueが示すアドレスにLONG値を格納する
				LONG* plValue = (LONG*)pValue;
				*plValue = fmtValue.longValue;
			}
			break;
		case PDH_FMT_DOUBLE:
			if( dwValueSize != sizeof(double) )
			{
				assert( !"受け取る値の型の種類に対して値受け取りバッファーのサイズが不正" );
				return false;
			}
			else
			{
				// pValueが示すアドレスにdouble値を格納する
				double* pdValue = (double*)pValue;
				*pdValue = fmtValue.doubleValue;
			}
			break;
		case PDH_FMT_ANSI:
		case PDH_FMT_UNICODE:
		case PDH_FMT_LARGE:
		default:
			assert( !"未対応" );
			return false;
	}
	return true;
}


/**
 * 指定されたインスタンスの ID Process パフォーマンスカウンタ値を取得します。
 *
 * @param インスタンス名
 * @return カウンタの値の取得に成功した場合はパフォーマンスカウンタ値、失敗した場合は -1
 */
static
LONG GetIDProcess( LPTSTR pszTargetInstanceName )
{
	LONG value;
	if( !GetPdhCounterValue( pszTargetInstanceName, _T("ID Process"), PDH_FMT_LONG, &value, sizeof(value) ) )
	{
		return INVALID_ID_PROCESS;
	}
	
	return value;
}


/**
 * カレントインスタンス名を取得します。
 *
 * @param pszCurrentInstanceNameDest インスタンス名格納先
 * @param dwCurrentInstanceNameSize インスタンス名の最大長
 * @return インスタンス名の取得に成功した場合は true 、失敗した場合は false
 */
static
bool GetCurrentInstanceName( LPTSTR pszCurrentInstanceNameDest, DWORD dwCurrentInstanceNameSize )
{
	if( NULL == pszCurrentInstanceNameDest || 0 == dwCurrentInstanceNameSize )
	{
		return false;
	}

	bool bResult = false;

	// インスタンスリスト用の領域として必要な大きさを求める
	DWORD	dwCounterListSize = 0;
	LPTSTR	pmszInstanceList = (LPTSTR)TactfulMalloc( INITIALPATHSIZE * sizeof(TCHAR) );
	DWORD	dwInstanceListSize = 0;
	PDH_STATUS  pdhStatus;
	
	pdhStatus = PdhExpandCounterPath(_T("\\Process(*)\\ID Process"), pmszInstanceList, &dwInstanceListSize);
	if( PDH_MORE_DATA == pdhStatus)
	{
		TactfulFree(pmszInstanceList);
		
		// インスタンスリスト用の領域の確保
		pmszInstanceList = (LPTSTR)TactfulMalloc( (dwInstanceListSize + INITIALPATHSIZE) * sizeof(TCHAR) );
		if (NULL == pmszInstanceList )
		{
			assert( !"失敗" );
			goto LABEL_END;
		}
		
		pdhStatus = PdhExpandCounterPath(_T("\\Process(*)\\ID Process"), pmszInstanceList, &dwInstanceListSize);

	}

	if( PDH_CSTATUS_VALID_DATA != pdhStatus)
	{
		assert( !"失敗" );
		goto LABEL_END;
	}
	
	// プロセスIDをキーとして、カレントインスタンス名を探す
	LONG lCurrentProcessId = GetCurrentProcessId();
  
	// インスタンスリストから取り出すインスタンス名用の領域の確保
	LPTSTR pszBuffer = (LPTSTR)TactfulMalloc( dwCurrentInstanceNameSize * sizeof(TCHAR) );
	LPTSTR pszInstanceName = pmszInstanceList;
	fflush(stdout);
	while( *pszInstanceName )
	{
		LPTSTR pszInstanceNameStart = strstr(pszInstanceName, _T("\\Process(")) + 9 * sizeof(TCHAR);
		LPTSTR pszInstanceNameEnd = strstr(pszInstanceNameStart, _T(")"));
		_tcsncpy_s( pszBuffer, dwCurrentInstanceNameSize, pszInstanceNameStart, pszInstanceNameEnd - pszInstanceNameStart);

		fflush(stdout);
		if( lCurrentProcessId == GetIDProcess( pszBuffer ) )
		{
			_tcsncpy_s( pszCurrentInstanceNameDest, dwCurrentInstanceNameSize, pszBuffer, dwCurrentInstanceNameSize );

			bResult = true;
			goto LABEL_END;
		}

		pszInstanceName += _tcslen(pszInstanceName) + 1;
	}
LABEL_END:
	// メモリの後処理
	TactfulFree( pszBuffer );
	TactfulFree( pmszInstanceList );

	return bResult;
}


/**
 * カウンタハンドル作成し、登録します。
 *
 * @param pszInstanceName インスタンス名
 * @param pszCounterName カウンタ名
 * @param hCounter カウンタハンドル格納先
 * @return カウンタハンドルの登録に成功した場合は true 、失敗した場合は false
 */
static
bool AddCounter( LPTSTR pszInstanceName, LPTSTR pszCounterName, PDH_HCOUNTER &hCounter )
{
	// 入力チェック
	if( NULL == pszInstanceName || NULL == pszCounterName )
	{
		assert( !"入力エラー" );
		return false;
	}

	// カウンターパスの作成
	TCHAR szCounterPath[COUNTER_PATH_SIZE];
	if( !MakeCounterPath( pszInstanceName, pszCounterName, szCounterPath, COUNTER_PATH_SIZE ) )
	{
		return false;
	}

	// 作成したカウンターパスを要求ハンドルに登録。カウンターハンドルを得ておく。
	if( ERROR_SUCCESS != PdhAddCounter( hQuery, szCounterPath, 0, &hCounter ) )
	{
		return false;
	}

	return true;
}

/**
 * プロセスの値を取得するカウンタを登録します。
 *
 * @param lpszCurrentInstanceName このプロセスのインスタンス名
 */
static void AddCounterForProcess( LPCTSTR lpszCurrentInstanceName )
{
	AddCounter( szCurrentInstanceName, _T("% User Time"), hCounterSysProcUser );
	AddCounter( szCurrentInstanceName, _T("% Privileged Time"), hCounterSysProcSys );
	AddCounter( szCurrentInstanceName, _T("Page Faults/sec"), hCounterSysMajFlt );
	AddCounter( szCurrentInstanceName, _T("Virtual Bytes"), hCounterSysVSize );
	AddCounter( szCurrentInstanceName, _T("Working Set"), hCounterSysRSS );
	AddCounter( szCurrentInstanceName, _T("Thread Count"), hCounterSysNumThreads );
	AddCounter( szCurrentInstanceName, _T("Handle Count"), hCounterProcNumFDs );
}

/**
 * 登録したカウンタハンドルを削除します。
 */
static void RemoveCounterForProcess()
{
	PdhRemoveCounter(hCounterSysProcUser);
	PdhRemoveCounter(hCounterSysProcSys);
	PdhRemoveCounter(hCounterSysMajFlt);
	PdhRemoveCounter(hCounterSysVSize);
	PdhRemoveCounter(hCounterSysRSS);
	PdhRemoveCounter(hCounterSysNumThreads);
	PdhRemoveCounter(hCounterProcNumFDs);
}


/****************************************************************************/
/*                        Javaから呼ばれる関数の定義                        */
/****************************************************************************/


JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_openQuery
  (JNIEnv *env, jobject obj) {
	if ( PdhOpenQuery(NULL, 0, &hQuery) == ERROR_SUCCESS )
  	{
		return true;
	}
	return false;
}

/**
 * 登録されているカウンタの値を計測します。
 *
 * @return 成功した場合は true 、失敗した場合は false
 */
JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_collectQueryData
  (JNIEnv *env, jobject obj){
	if ( PdhCollectQueryData( hQuery ) == ERROR_SUCCESS )
	{
		return true;
	}
  	return false;
}

/**
 * CPU使用率（システム）を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueSysCPUSys
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysCPUSys);
}

/*
 * CPU使用率（ユーザ）を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueSysCPUUser
  (JNIEnv *env, jobject obj){
  	return GetFormattedCounterValueByDouble(hCounterSysCPUUser);
}

/*
 * 物理メモリ（最大）を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getMemoryTotal
  (JNIEnv *env, jobject obj){
	MEMORYSTATUSEX state;
	state.dwLength = sizeof(state);
	GlobalMemoryStatusEx(&state);
	return (jdouble)(state.ullTotalPhys);
}

/*
 * 物理メモリ（空き）を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueMemAvailable
  (JNIEnv *env, jobject obj){
	MEMORYSTATUSEX state;
	state.dwLength = sizeof(state);
	GlobalMemoryStatusEx(&state);
	return (jdouble)(state.ullAvailPhys);
}

/*
 * ページファイル使用率を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValuePageFileUsage
  (JNIEnv *env, jobject obj){
	MEMORYSTATUSEX state;
	state.dwLength = sizeof(state);
	GlobalMemoryStatusEx(&state);
	if (state.ullTotalPageFile == 0)
	{
		return (jdouble)0.0;
	}
	return (jdouble)(state.ullAvailPageFile * 100.0 / state.ullTotalPageFile);
}

/*
 * ページファイル使用量を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValuePageFileBytes
  (JNIEnv *env, jobject obj){
	MEMORYSTATUSEX state;
	state.dwLength = sizeof(state);
	GlobalMemoryStatusEx(&state);
	return (jdouble)(state.ullAvailPageFile);
}

/*
 * システム全体の仮想メモリ使用量を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueVirtualBytes
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysVirtualBytes);
}

/*
 * システム全体の使用FD量を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueSystemFDs
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysNumFDs);
}

/*
 * ページインを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValuePageIn
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysPageIn);
}

/*
 * ページアウトを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValuePageOut
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysPageOut);
}

/*
 * プロセスのUser Timeを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueProcessUserTime
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDoubleNoCap100(hCounterSysProcUser);
}

/*
 * プロセスのPrivileged Timeを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueProcessPrivilegedTime
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDoubleNoCap100(hCounterSysProcSys);
}

/*
 * メジャーフォールトを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueMajFlt
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysMajFlt);
}

/*
 * VSizeを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueVSize
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysVSize);
}

/*
 * RSSを取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueRSS
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysRSS);
}

/*
 * プロセスのスレッド数を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueNumThreads
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterSysNumThreads);
}

/*
 * プロセスの使用FD数を取得
 */
JNIEXPORT jdouble JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_getFormattedCounterValueProcFDs
  (JNIEnv *env, jobject obj){
	return GetFormattedCounterValueByDouble(hCounterProcNumFDs);
}

/*
 * クエリーの使用を終了
 */
JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_closeQuery
  (JNIEnv *env, jobject obj){
	PdhCloseQuery( hQuery );
	return true;
}

/**
 * クエリーを追加
 *
 * @return 成功した場合は true 、失敗した場合は false
 */
JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_addCounter
(JNIEnv *env, jobject obj, jstring counterPath){
        // 3回試行する。
	if( !GetCurrentInstanceName( szCurrentInstanceName, INSTANCE_NAME_SIZE ) )
	{
		if( !GetCurrentInstanceName( szCurrentInstanceName, INSTANCE_NAME_SIZE ) )
		{
			if( !GetCurrentInstanceName( szCurrentInstanceName, INSTANCE_NAME_SIZE ) )
			{
			   return false;
			}
		}
	}

	AddCounterForProcess(szCurrentInstanceName);

	PdhAddCounter( hQuery, "\\Processor(_Total)\\% Privileged Time", 0, &hCounterSysCPUSys );
	PdhAddCounter( hQuery, "\\Processor(_Total)\\% User Time", 0, &hCounterSysCPUUser );
	PdhAddCounter( hQuery, "\\Paging File(_Total)\\% Usage", 0, &hCounterSysPageUsage );
	PdhAddCounter( hQuery, "\\Process(_Total)\\Page File Bytes", 0, &hCounterSysPageBytes );
	PdhAddCounter( hQuery, "\\Memory\\Pages Input/sec", 0, &hCounterSysPageIn );
	PdhAddCounter( hQuery, "\\Memory\\Pages Output/sec", 0, &hCounterSysPageOut );
	PdhAddCounter( hQuery, "\\Process(_Total)\\Handle Count", 0, &hCounterSysNumFDs );

  return true;
}

/**
 * 必要であれば、ハンドルを更新します。
 *
 * @return ハンドルを更新した場合は true 、更新しなかった場合は false
 */
JNIEXPORT jboolean JNICALL Java_jp_co_acroquest_endosnipe_javelin_resource_proc_PerfCounter_updateHandles
(JNIEnv *env, jobject obj){

	TCHAR szNewInstanceName[INSTANCE_NAME_SIZE];
	if( !GetCurrentInstanceName( szNewInstanceName, INSTANCE_NAME_SIZE ) )
	{
	   return false;
	}

	if ( _tcscmp(szCurrentInstanceName, szNewInstanceName) != 0 )
	{
		_tcscpy(szCurrentInstanceName, szNewInstanceName);
		RemoveCounterForProcess();
		AddCounterForProcess(szCurrentInstanceName);
		return true;
	}

	return false;
}

