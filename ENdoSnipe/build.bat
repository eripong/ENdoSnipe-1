@echo off

rem ---------------------------------------------------------------------------
rem この環境変数はビルドスクリプトによって自動的に build.properties と
rem 各プロジェクトの MANIFEST.INF へ反映されます
rem VERを変更する際は、以下のファイルも同時にバージョンを変更する必要があります。
rem javelin.pro,pom.xml,arrowvision.pro,bottleneckeye.pro
rem ---------------------------------------------------------------------------
set VER=5.0.0
set BUILD=016
rem ---------------------------------------------------------------------------

if exist "C:\Program Files (x86)" goto 64BIT

:32BIT
echo 32 bit environment
set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_22
set JAVA6_HOME=C:\Program Files\Java\jdk1.6.0_35
goto SETTING

:64BIT
echo 64 bit environment
set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.5.0_22
set JAVA6_HOME=C:\Program Files\Java\jdk1.6.0_35

:SETTING


set WORK_DIR=%~dp0deploy
set TAGS=Version_%VER%-%BUILD%_build
set PATH=%JAVA_HOME%\bin;%PATH%
set SVN_PATH=https://wisteria.proma-c.com/svn/ENdoSnipe
set SVN_TRUNK_PATH=%SVN_PATH%/branches/Ver5.0/
set SVN_TAGS_PATH=%SVN_PATH%/tags/ENdoSnipe/
set SVN_DOC_PATH=%SVN_TRUNK_PATH%/Document
set SVN_SRC_PATH=%SVN_TRUNK_PATH%/MasterSource

if exist "%WORK_DIR%" rmdir "%WORK_DIR%" /S /Q


echo ビルドを開始します。
echo ===============================
echo ●JAVAバージョン
java -version
echo ●タグ
echo %TAGS%
echo ===============================

pause

echo SVN上でビルド番号"%TAGS%"でタグを発行します。

svn mkdir %SVN_TAGS_PATH%%TAGS%/              -m %TAGS%-タグディレクトリ作成


echo ------------------------------------------------------------
echo ビルド (JARファイルの生成)
echo ------------------------------------------------------------

svn copy %SVN_DOC_PATH% %SVN_TAGS_PATH%%TAGS% -m %TAGS%-ドキュメントコピー
svn copy %SVN_SRC_PATH% %SVN_TAGS_PATH%%TAGS% -m %TAGS%-ソースコピー

echo コピーが完了しました。
echo 続いて、タグを発行したファイルを、ローカルの"%WORK_DIR%\svn"にチェックアウトします。

svn co %SVN_TAGS_PATH%%TAGS%/MasterSource "%WORK_DIR%\svn"

cd /d "%WORK_DIR%\svn\ENdoSnipe"

echo Antを実行します。

call build_java.bat

echo ビルドが完了しました。
echo WebDashboard をビルドします。

set JAVA_HOME_BAK=%JAVA_HOME%
set JAVA_HOME=%JAVA6_HOME%

cd ..\WebDashboard

call ant dist

echo ビルドが完了しました。

cd ..\ENdoSnipe
set JAVA_HOME=%JAVA_HOME_BAK%


echo ------------------------------------------------------------
echo 更新ファイルのコミット
echo ------------------------------------------------------------
echo ビルド時に更新されたファイルを %SVN_TAGS_PATH%%TAGS% へコミットします。

cd /d "%WORK_DIR%\svn"
svn commit . -m %TAGS%-ビルド時更新ファイルのコミット

echo コミットが完了しました。

echo 作業コピーを %SVN_SRC_PATH% に切り替えます。
svn switch %SVN_SRC_PATH%
echo 切り替えが完了しました。

echo ビルド時に更新されたファイルを作業コピーにマージします。
svn merge --force %SVN_TAGS_PATH%%TAGS%/MasterSource .
echo マージが完了しました。

echo マージ結果をコミットします。
svn commit . -m %TAGS%-ビルド時に更新されたファイルのコミット
echo コミットが完了しました。

echo ------------------------------------------------------------
echo デプロイファイルのコミット
echo ------------------------------------------------------------

svn mkdir %SVN_TAGS_PATH%%TAGS%/Product -m %TAGS%-成果物用ディレクトリ作成
svn mkdir %SVN_TAGS_PATH%%TAGS%/Product/Software -m %TAGS%-成果物用ディレクトリ作成

cd /d "%WORK_DIR%"

svn co %SVN_TAGS_PATH%%TAGS%/Product

cd /d "%WORK_DIR%\Product\Software"
copy "%WORK_DIR%\svn\ENdoSnipe\release\*" .
svn add *
svn commit . -m %TAGS%-成果物コミット

echo すべてのビルドプロセスが完了しました。
pause
